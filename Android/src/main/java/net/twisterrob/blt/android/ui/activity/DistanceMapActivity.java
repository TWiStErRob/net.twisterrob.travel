package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import org.slf4j.*;

import android.graphics.Bitmap;
import android.os.*;
import android.os.AsyncTask.Status;
import android.preference.PreferenceManager;
import android.support.design.widget.*;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.Marker;

import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.view.*;
import net.twisterrob.android.view.ViewProvider.StaticViewProvider;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.db.model.NetworkNode;
import net.twisterrob.blt.android.ui.activity.DistanceOptionsFragment.ConfigsUpdatedListener;

public class DistanceMapActivity extends AppCompatActivity {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapActivity.class);

	private static final int MAP_PADDING = 50;

	private GoogleMap m_map;
	private DistanceMapGeneratorConfig distanceConfig = new DistanceMapGeneratorConfig()
			.startWalkMinutes(10)
			.minutes(25);
	private DistanceMapDrawerConfig drawConfig = new DistanceMapDrawerConfig()
			.dynamicColor(true);
	private BottomSheetBehavior behavior;
	private NearestStationsFragment nearestFragment;
	private DistanceOptionsFragment optionsFragment;
	private DrawerLayout drawers;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_map);

		FragmentManager fm = getSupportFragmentManager();
		SupportMapFragment mapFragment = (SupportMapFragment)fm.findFragmentById(R.id.map);
		m_map = mapFragment.getMap();
		m_map.setMyLocationEnabled(true);
		m_map.getUiSettings().setZoomControlsEnabled(false);
		m_map.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng latlng) {
				reDraw(latlng);
			}
		});
		m_map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override public boolean onMarkerClick(Marker marker) {
				if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
					behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
				}
				return false;
			}
		});

		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		drawers = (DrawerLayout)findViewById(R.id.drawer);

		nearestFragment = (NearestStationsFragment)fm.findFragmentById(R.id.distance_bottom_sheet);
		optionsFragment = (DistanceOptionsFragment)fm.findFragmentById(R.id.distance_drawer);
		optionsFragment.bindConfigs(distanceConfig, drawConfig);
		optionsFragment.setConfigsUpdatedListener(new ConfigsUpdatedListener() {
			@Override public void onConfigsUpdated() {
				reDraw(m_lastStartPoint);
			}
		});
		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
		behavior = BottomSheetBehavior.from(findViewById(R.id.distance_bottom_sheet));
		behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		behavior.setBottomSheetCallback(new MultiBottomSheetCallback.Builder()
				//.add(new LoggingBottomSheetCallback())
				.add(new ShowOnlyWhenSheetHidden(new StaticViewProvider(fab)))
				//.add(new BottomMarginAdjuster(new SupportFragmentViewProvider(mapFragment), false))
				.build());
		fab.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				//noinspection WrongConstant it's not a constant, but it is a gravity field
				drawers.openDrawer(((DrawerLayout.LayoutParams)optionsFragment.getView().getLayoutParams()).gravity);
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
		if (latlng == null) {
			return;
		}
		LOG.trace("reDraw({}) / task: {}", latlng, AndroidTools.toString(m_redrawTask));
		if (m_redrawTask == null) {
			m_redrawTask = new RedrawAsyncTask(m_distanceMapGenerator, m_distanceMapDrawer);
		}
		if (m_redrawTask.getStatus() != Status.PENDING) {
			m_redrawTask.cancel(true);
			m_redrawTask = null;
			reDraw(latlng);
			return;
		}

		m_lastStartPoint = latlng;
		nearestFragment.updateLocation(latlng, null);
		try {
			AndroidTools.executePreferParallel(m_redrawTask, latlng);
		} catch (Exception ex) {
			LOG.warn("Exception while executing tasks", ex);
			Toast.makeText(DistanceMapActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
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
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
			);
			m_markersStart.add(marker);
		}
	}

	private void updateNearestStations(Collection<NetworkNode> startNodes) {
		nearestFragment.updateNearestStations(startNodes);
		if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("showNearest", true)) {
			// force the UI to become collapsed, this post-hack gives the most consistent results
			behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
			getWindow().getDecorView().post(new Runnable() {
				@Override public void run() {
					behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
				}
			});
		}
	}

	@SuppressWarnings("unused")
	private void addMarkers(Iterable<NetworkNode> nodes) {
		for (NetworkNode node : nodes) {
			LatLng ll = LocationUtils.toLatLng(node.getLocation());
			m_map.addMarker(new MarkerOptions().title(String.valueOf(node.getID())).position(ll));
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
}
