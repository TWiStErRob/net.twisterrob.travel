package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import org.slf4j.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.*;
import android.os.*;
import android.os.AsyncTask.Status;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.Marker;

import net.twisterrob.android.utils.concurrent.SimpleSafeAsyncTask;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.view.*;
import net.twisterrob.android.view.ViewProvider.StaticViewProvider;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;
import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.Location;

import static net.twisterrob.blt.android.R.id.*;

public class DistanceMapActivity extends AppCompatActivity {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapActivity.class);

	private static final int MAP_PADDING = 50;

	private GoogleMap m_map;
	private DistanceMapGeneratorConfig distanceConfig = new DistanceMapGeneratorConfig()
			.startWalkMinutes(10)
			.minutes(25);
	private DistanceMapDrawerConfig drawConfig = new DistanceMapDrawerConfig()
			.dynamicColor(true);
	private TextSwitcher droppedPin;
	private ViewGroup nearestStations;
	private BottomSheetBehavior behavior;
	private GeocoderTask m_geocoderTask;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_map);

		final SupportMapFragment mapFragment =
				(SupportMapFragment)getSupportFragmentManager().findFragmentById(map);
		m_map = mapFragment.getMap();
		m_map.setMyLocationEnabled(true);
		m_map.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng latlng) {
				reDraw(latlng);
			}
		});

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		droppedPin = (TextSwitcher)findViewById(R.id.distance_map_dropped_pin);
		droppedPin.setInAnimation(this, android.R.anim.fade_in);
		droppedPin.setOutAnimation(this, android.R.anim.fade_out);
		nearestStations = (ViewGroup)findViewById(R.id.layout$distance_map$nearest_stations);
		nearestStations.removeAllViews();
		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
		behavior = BottomSheetBehavior.from(findViewById(R.id.design_bottom_sheet));
		behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		behavior.setBottomSheetCallback(new MultiBottomSheetCallback.Builder()
				//.add(new LoggingBottomSheetCallback())
				.add(new ShowOnlyWhenSheetHidden(new StaticViewProvider(fab)))
				//.add(new BottomMarginAdjuster(new SupportFragmentViewProvider(mapFragment), false))
				.build());
		fab.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
			}
		});

		new AsyncTask<Void, Void, Set<NetworkNode>>() {
			@Override protected Set<NetworkNode> doInBackground(Void... params) {
				return App.getInstance().getDataBaseHelper().getTubeNetwork();
			}
			@Override protected void onPostExecute(Set<NetworkNode> nodes) {
				super.onPostExecute(nodes);
				setNodes(nodes);
			}
		}.execute((Void[])null);
	}

	private DistanceMapGenerator m_distanceMapGenerator;
	private DistanceMapDrawerAndroid m_distanceMapDrawer;
	private GroundOverlay m_groundOverlay;
	private List<Marker> m_markersStart = new LinkedList<>();

	private void setNodes(Set<NetworkNode> nodes) {
		LOG.trace("setNodes(nodes:{})", nodes.size());
		m_distanceMapGenerator = new DistanceMapGenerator(nodes, distanceConfig);
		m_distanceMapDrawer = new DistanceMapDrawerAndroid(nodes, drawConfig);
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(m_distanceMapDrawer.getBounds(), MAP_PADDING);
		m_map.moveCamera(cu);
		// distance map below
		Bitmap emptyMap = m_distanceMapDrawer.draw(Collections.<NetworkNode, Double>emptyMap());
		m_groundOverlay = m_map.addGroundOverlay(new GroundOverlayOptions()
				.positionFromBounds(
						m_distanceMapDrawer.getBounds())
				.transparency(0.0f)
				.image(BitmapDescriptorFactory.fromBitmap(emptyMap))
		);
		// tube map above
		m_map.addGroundOverlay(new GroundOverlayOptions()
				.positionFromBounds(m_distanceMapDrawer.getBounds())
				.transparency(0.3f)
				.image(BitmapDescriptorFactory.fromBitmap(new TubeMapDrawer(nodes).draw(nodes)))
		);
	}

	private AsyncTask<LatLng, Void, Bitmap> m_redrawTask;

	private LatLng m_lastStartPoint;
	@SuppressWarnings("Duplicates")
	private void reDraw(LatLng latlng) {
		LOG.trace("reDraw({}) / task status: {}", latlng, m_redrawTask == null? null : m_redrawTask.getStatus());
		if (m_redrawTask == null) {
			m_redrawTask = new RedrawAsyncTask(m_distanceMapGenerator, m_distanceMapDrawer);
		}
		if (m_redrawTask.getStatus() != Status.PENDING) {
			m_redrawTask.cancel(true);
			m_redrawTask = null;
			reDraw(latlng);
			return;
		}

		LOG.trace("geoCoder({}) / task status: {}", latlng, m_geocoderTask == null? null : m_geocoderTask.getStatus());
		if (m_geocoderTask == null) {
			m_geocoderTask = new GeocoderTask(this);
		}
		if (m_geocoderTask.getStatus() != Status.PENDING) {
			m_geocoderTask.cancel(true);
			m_geocoderTask = null;
			reDraw(latlng);
			return;
		}

		m_lastStartPoint = latlng;
		updateLocation(latlng, null);
		try {
			AndroidTools.executePreferParallel(m_redrawTask, latlng);
			AndroidTools.executePreferParallel(m_geocoderTask, latlng);
		} catch (Exception ex) {
			LOG.warn("Exception while executing tasks", ex);
			Toast.makeText(DistanceMapActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void updateLocation(@NonNull LatLng latlng, @Nullable Address address) {
		LOG.trace("Got location: {}, address: {}", latlng, AndroidTools.toString(address));
		String addressString = LocationUtils.getVagueAddress(address);
		if (addressString == null) {
			String pin = String.format(Locale.getDefault(),
					"Location at %.4f, %.4f", latlng.latitude, latlng.longitude);
			droppedPin.setCurrentText(pin);
		} else {
			droppedPin.setText(addressString);
		}
	}

	private void updateDistanceMap(Bitmap map) {
		LOG.trace("updateDistanceMap({})", map);
		m_groundOverlay.setImage(BitmapDescriptorFactory.fromBitmap(map));
		Collection<NetworkNode> startNodes = m_distanceMapGenerator.getStartNodes();
		reCreateMarkers(startNodes);
		updateNearestStations(startNodes);
	}

	private void reCreateMarkers(Collection<NetworkNode> startNodes) {
		LOG.trace("reCreateMarkers");
		for (Marker marker : m_markersStart) {
			marker.remove();
		}
		for (NetworkNode startNode : startNodes) {
			LOG.trace("Creating marker for {}", startNode);
			Marker marker = m_map.addMarker(new MarkerOptions()
					.title(startNode.getName())
					.position(LocationUtils.toLatLng(startNode.getLocation()))
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			m_markersStart.add(marker);
		}
		if (m_lastStartPoint != null) {
			LOG.trace("Creating marker for starting point {}", m_lastStartPoint);
			Marker marker = m_map.addMarker(new MarkerOptions()
					.title("Starting point")
					.position(m_lastStartPoint)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
			m_markersStart.add(marker);
		}
	}
	private void updateNearestStations(Collection<NetworkNode> startNodes) {
		nearestStations.removeAllViews();
		for (StationWithDistance station : toStations(startNodes)) {
			LOG.trace("Creating nearest station for {}", station);
			View view = getLayoutInflater().inflate(R.layout.item_station, nearestStations, false);
			station.setName(station.getName() + " (" + Math.round(station.getDistance()) + "m)");
			new StationAdapter.ViewHolder(view).bind(station, null);
			nearestStations.addView(view);
		}
		if (nearestStations.getChildCount() == 0) {
			TextView empty = new TextView(this);
			empty.setText("No stations found around here.");
			nearestStations.addView(empty);
		}
		// force the UI to become collapsed, this post-hack gives the most consistent results
		behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		nearestStations.post(new Runnable() {
			@Override public void run() {
				behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			}
		});
	}
	private Collection<StationWithDistance> toStations(Collection<NetworkNode> nodes) {
		Map<String, StationWithDistance> stations = new HashMap<>();
		Location distanceReference = LocationUtils.fromLatLng(m_lastStartPoint);
		for (NetworkNode node : nodes) {
			String key = node.getName();
			StationWithDistance station = stations.get(key);
			if (station == null) {
				station = new StationWithDistance();
				station.setName(node.getName());
				station.setLocation(node.getLocation());
				station.setLines(new ArrayList<Line>());
				station.setDistance(LocationUtils.distance(distanceReference, station.getLocation()));
				stations.put(key, station);
			}
			station.getLines().add(node.getLine());
		}
		for (StationWithDistance station : stations.values()) {
			int[] stopCounts = new int[StopType.values().length];
			for (Line line : station.getLines()) {
				stopCounts[line.getDefaultStopType().ordinal()]++;
			}
			int max = 0;
			for (int i = max + 1; i < stopCounts.length; i++) {
				// picks the first if there's a tie
				if (stopCounts[max] < stopCounts[i]) {
					max = i;
				}
			}
			station.setType(StopType.values()[max]);
		}
		List<StationWithDistance> result = new ArrayList<>(stations.values());
		Collections.sort(result, new Comparator<StationWithDistance>() {
			@Override public int compare(StationWithDistance lhs, StationWithDistance rhs) {
				return Double.compare(lhs.getDistance(), rhs.getDistance());
			}
		});
		return result;
	}

	@SuppressWarnings("unused")
	private void addMarkers(Iterable<NetworkNode> nodes) {
		for (NetworkNode node : nodes) {
			LatLng ll = LocationUtils.toLatLng(node.getLocation());
			m_map.addMarker(new MarkerOptions().title(String.valueOf(node.getID())).position(ll));
		}
	}

	private static class StationWithDistance extends Station {
		private double distance;
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
	}

	private final class RedrawAsyncTask extends AsyncTask<LatLng, Void, Bitmap> {
		private DistanceMapGenerator m_mapGenerator;
		private DistanceMapDrawerAndroid m_mapDrawer;

		private RedrawAsyncTask(DistanceMapGenerator distanceMapGenerator, DistanceMapDrawerAndroid distanceMapDrawer) {
			m_mapGenerator = distanceMapGenerator;
			m_mapDrawer = distanceMapDrawer;
		}

		@Override protected void onPreExecute() {
			if (m_mapGenerator == null || m_mapDrawer == null) {
				cancel(false);
				throw new IllegalStateException(
						"Someone has quick fingers, Tube network is not ready, please wait and try again.");
			}
		}

		@Override protected Bitmap doInBackground(LatLng... params) {
			LOG.trace("Drawing map for {}", (Object)params);
			Map<NetworkNode, Double> distanceMap = m_mapGenerator.generate(LocationUtils.fromLatLng(params[0]));
			if (isCancelled()) {
				return null;
			}
			//noinspection UnnecessaryLocalVariable easier to check the value in debug
			Bitmap overlay = m_mapDrawer.draw(distanceMap);
			return overlay;
		}

		@Override protected void onPostExecute(Bitmap map) {
			updateDistanceMap(map);
		}
	}

	private final class GeocoderTask extends SimpleSafeAsyncTask<LatLng, Void, Address> {
		private final Geocoder geocoder;
		private GeocoderTask(Context context) {
			if (Geocoder.isPresent()) {
				geocoder = new Geocoder(context);
			} else {
				LOG.warn("Geocoding not available.");
				geocoder = null;
			}
		}
		@Override protected @Nullable Address doInBackground(LatLng latlng) throws Exception {
			if (geocoder != null) {
				List<Address> location = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
				if (location != null && !location.isEmpty()) {
					return location.get(0);
				}
			}
			return null;
		}
		@Override protected void onResult(@Nullable Address address, LatLng latlng) {
			updateLocation(latlng, address);
		}
		@Override protected void onError(@NonNull Exception ex, LatLng latlng) {
			LOG.warn("Cannot determine location for {}", latlng, ex);
			updateLocation(latlng, null);
		}
	}
}
