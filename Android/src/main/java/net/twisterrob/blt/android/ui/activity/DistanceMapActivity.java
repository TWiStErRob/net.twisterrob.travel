package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import org.slf4j.*;

import android.graphics.Bitmap;
import android.os.*;
import android.os.AsyncTask.Status;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.Marker;

import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.db.model.NetworkNode;

public class DistanceMapActivity extends FragmentActivity {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapActivity.class);

	private static final int MAP_PADDING = 50;

	private GoogleMap m_map;
	private DistanceMapGeneratorConfig distanceConfig = new DistanceMapGeneratorConfig()
			.startWalkMinutes(10)
			.minutes(25);
	private DistanceMapDrawerConfig drawConfig = new DistanceMapDrawerConfig()
			.dynamicColor(true);

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_map);

		m_map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		m_map.setMyLocationEnabled(true);
		m_map.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng latlng) {
				reDraw(latlng);
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

	protected void setNodes(Set<NetworkNode> nodes) {
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
	protected void reDraw(LatLng latlng) {
		m_lastStartPoint = latlng;
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

		try {
			m_redrawTask.execute(latlng);
		} catch (Exception ex) {
			LOG.warn("Exception while executing redraw task", ex);
			Toast.makeText(DistanceMapActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	protected void updateDistanceMap(Bitmap map) {
		LOG.trace("updateDistanceMap({})", map);
		m_groundOverlay.setImage(BitmapDescriptorFactory.fromBitmap(map));
		Collection<NetworkNode> startNodes = m_distanceMapGenerator.getStartNodes();
		reCreateMarkers(m_markersStart, startNodes);
	}

	private void reCreateMarkers(Collection<Marker> markers, Collection<NetworkNode> startNodes) {
		LOG.trace("reCreateMarkers");
		for (Marker marker : markers) {
			marker.remove();
		}
		for (NetworkNode startNode : startNodes) {
			Marker marker = m_map.addMarker(new MarkerOptions()
					.title(startNode.getName())
					.position(LocationUtils.toLatLng(startNode.getLocation()))
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			markers.add(marker);
		}
		if (m_lastStartPoint != null) {
			Marker marker = m_map.addMarker(new MarkerOptions()
					.title("Starting point")
					.position(m_lastStartPoint)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
			markers.add(marker);
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
		private final Logger LOG = LoggerFactory.getLogger(RedrawAsyncTask.class);
		private DistanceMapGenerator m_mapGenerator;
		private DistanceMapDrawerAndroid m_mapDrawer;

		public RedrawAsyncTask(DistanceMapGenerator distanceMapGenerator, DistanceMapDrawerAndroid distanceMapDrawer) {
			m_mapGenerator = distanceMapGenerator;
			m_mapDrawer = distanceMapDrawer;
		}

		@Override protected void onPreExecute() {
			super.onPreExecute();
			if (m_mapGenerator == null || m_mapDrawer == null) {
				cancel(false);
				throw new IllegalStateException(
						"Someone has quick fingers, Tube network is not ready, please wait and try again.");
			}
		}

		@Override protected Bitmap doInBackground(LatLng... params) {
			LOG.trace("doInBackground({})", (Object)params);
			Map<NetworkNode, Double> distanceMap = m_mapGenerator.generate(LocationUtils.fromLatLng(params[0]));
			if (isCancelled()) {
				return null;
			}
			Bitmap overlay = m_mapDrawer.draw(distanceMap);
			return overlay;
		}

		@Override protected void onPostExecute(Bitmap map) {
			super.onPostExecute(map);
			updateDistanceMap(map);
		}
	}
}
