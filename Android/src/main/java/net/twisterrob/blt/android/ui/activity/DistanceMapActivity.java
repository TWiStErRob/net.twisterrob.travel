package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import org.slf4j.*;

import android.content.SharedPreferences;
import android.graphics.*;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.annotation.*;
import android.support.design.widget.*;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.Marker;

import net.twisterrob.android.utils.concurrent.SimpleAsyncTask;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.view.*;
import net.twisterrob.android.view.ViewProvider.StaticViewProvider;
import net.twisterrob.android.view.layout.DoAfterLayout;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.db.model.NetworkNode;
import net.twisterrob.blt.android.ui.activity.DistanceOptionsFragment.ConfigsUpdatedListener;
import net.twisterrob.blt.model.StopType;

import static net.twisterrob.blt.android.R.id.*;

public class DistanceMapActivity extends AppCompatActivity {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapActivity.class);

	private GoogleMap map;
	private DistanceMapGeneratorConfig distanceConfig = new DistanceMapGeneratorConfig();
	private DistanceMapDrawerConfig drawConfig = new DistanceMapDrawerConfig();
	private BottomSheetBehavior behavior;
	private NearestStationsFragment nearestFragment;
	private DistanceOptionsFragment optionsFragment;
	private ClickThroughDrawerLayout drawers;
	private Set<NetworkNode> tubeNetwork;
	private DrawAsyncTask drawTask;
	private LatLng lastStartPoint;

	@Override protected void onCreate(Bundle savedInstanceState) {
		int statusBarColor = ColorUtils.setAlphaComponent(ContextCompat.getColor(this, R.color.accent), 0x66);
		AndroidTools.setTranslucentStatusBar(getWindow(), statusBarColor);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_map);

		FragmentManager fm = getSupportFragmentManager();
		SupportMapFragment mapFragment = (SupportMapFragment)fm.findFragmentById(R.id.map);
		map = mapFragment.getMap();
		map.setMyLocationEnabled(true);
		zoomFullLondon();
		class MapInteractorListener implements OnMapClickListener, OnMarkerClickListener, OnMapLongClickListener {
			private Marker currentMarker;
			@Override public void onMapLongClick(LatLng latlng) {
				if (currentMarker != null) {
					currentMarker.hideInfoWindow();
					currentMarker = null;
				}
				reDraw(latlng);
			}
			@Override public void onMapClick(LatLng latlng) {
				if (currentMarker != null) {
					currentMarker = null;
					return;
				}
				reDraw(latlng);
			}
			@Override public boolean onMarkerClick(Marker marker) {
				currentMarker = marker;
				if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
					behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
				}
				return false;
			}
		}

		MapInteractorListener listener = new MapInteractorListener();
		map.setOnMapClickListener(listener);
		map.setOnMapLongClickListener(listener);
		map.setOnMarkerClickListener(listener);

		AndroidTools.accountForStatusBar(findViewById(R.id.toolbar_container));
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
		drawers = (ClickThroughDrawerLayout)findViewById(drawer);
		new DoAfterLayout(drawers) {
			@Override protected void onLayout() {
				// when the map has more than 40% of the screen, allow using it even when the drawer is open
				if (optionsFragment.getView().getWidth() <= drawers.getWidth() * 0.60f) {
					drawers.setAllowClickThrough(true);
				}
			}
		};

		nearestFragment = (NearestStationsFragment)fm.findFragmentById(R.id.distance_bottom_sheet);
		optionsFragment = (DistanceOptionsFragment)fm.findFragmentById(R.id.distance_drawer);
		optionsFragment.bindConfigs(distanceConfig, drawConfig);
		optionsFragment.setConfigsUpdatedListener(new ConfigsUpdatedListener() {
			@Override public void onConfigsUpdated() {
				reDraw(lastStartPoint);
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
				//noinspection WrongConstant it's not a constant, and it is a gravity field
				drawers.openDrawer(((DrawerLayout.LayoutParams)optionsFragment.getView().getLayoutParams()).gravity);
			}
		});

		updateToolbarVisibility();
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

	private void zoomFullLondon() {
		LatLngBounds fullLondon = new LatLngBounds(new LatLng(51.342889, -0.611361), new LatLng(51.705232, 0.251388));
		Point screen = AndroidTools.getScreenSize(getWindowManager().getDefaultDisplay());
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(fullLondon, screen.x, screen.y, 0));
		// TODO map.moveCamera(CameraUpdateFactory.zoomIn()); // currently doesn't work maybe after updating play
	}

	public void updateToolbarVisibility() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean showToolbar = prefs.getBoolean("showToolbar", false);
		final View container = findViewById(R.id.toolbar_container);
		AndroidTools.displayedIf(container, showToolbar);
		new DoAfterLayout(drawers) {
			@Override protected void onLayout() {
				View bottomMostTopView = findViewById(R.id.toolbar_container);
				View topMostBottomView = findViewById(R.id.fab);
				int topMargin = bottomMostTopView.getBottom() + AndroidTools.getBottomMargin(bottomMostTopView);
				int bottomMargin = topMostBottomView.getTop() - AndroidTools.getTopMargin(topMostBottomView);
				bottomMargin = ((View)topMostBottomView.getParent()).getHeight() - bottomMargin;
				int statusBar = AndroidTools.getStatusBarHeight(DistanceMapActivity.this);
				map.setPadding(
						0, // left
						bottomMostTopView.getVisibility() != View.GONE? topMargin : statusBar, // top 
						0, // right
						topMostBottomView.getVisibility() != View.GONE? bottomMargin : 0 // bottom
				);
			}
		};
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_distance_map, menu);
//		AndroidTools.prepareSearch(this, menu, R.id.menu$options$search);
		return true;
	}

	@Override public void onBackPressed() {
		if (drawers.isDrawerOpen(GravityCompat.START) || drawers.isDrawerOpen(GravityCompat.END)) {
			drawers.closeDrawers();
			return;
		}
		if (behavior.isHideable() && behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
			behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
			return;
		}
		super.onBackPressed();
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		killTask();
	}
	private void killTask() {
		if (drawTask != null) {
			drawTask.cancel(true);
			drawTask = null;
		}
	}

	private GroundOverlay distanceMapOverlay;
	private List<Marker> m_markersStart = new LinkedList<>();

	private void setNodes(Set<NetworkNode> nodes) {
		tubeNetwork = nodes;
		LOG.trace("setNodes(nodes:{})", tubeNetwork.size());
		// TODO move to BG thread
		DistanceMapDrawerAndroid distanceMapDrawer = new DistanceMapDrawerAndroid(tubeNetwork, drawConfig);
//		map.moveCamera(CameraUpdateFactory.newLatLngBounds(distanceMapDrawer.getBounds(), 0)); // disabled for now
		// distance map below
		Bitmap emptyMap = distanceMapDrawer.draw(Collections.<NetworkNode, Double>emptyMap());
		distanceMapOverlay = map.addGroundOverlay(new GroundOverlayOptions()
				.positionFromBounds(distanceMapDrawer.getBounds())
				.transparency(0.0f)
				.image(BitmapDescriptorFactory.fromBitmap(emptyMap))
		);
		// tube map above
		map.addGroundOverlay(new GroundOverlayOptions()
				.positionFromBounds(distanceMapDrawer.getBounds())
				.transparency(0.3f)
				.image(BitmapDescriptorFactory.fromBitmap(new TubeMapDrawer(nodes).draw(nodes)))
		);
		if (lastStartPoint != null) {
			reDraw(lastStartPoint);
		}
	}

	@SuppressWarnings("Duplicates")
	private void reDraw(LatLng latlng) {
		if (latlng == null) {
			return;
		}
		lastStartPoint = latlng;
		nearestFragment.updateLocation(latlng, null);

		LOG.trace("reDraw({}) / task: {}", latlng, AndroidTools.toString(drawTask));
		killTask();
		if (tubeNetwork == null) {
			String message = "Someone has quick fingers, Tube network is not ready, please wait and try again.";
			Toast.makeText(DistanceMapActivity.this, message, Toast.LENGTH_LONG).show();
			return;
		}
		// don't set empty image for the ground overlay here because it flashes
		drawTask = new DrawAsyncTask(tubeNetwork, distanceConfig, drawConfig);
		AndroidTools.executePreferParallel(drawTask, latlng);
	}

	private void updateDistanceMap(Bitmap map, Collection<NetworkNode> startNodes) {
		LOG.trace("updateDistanceMap({})", map);
		if (map != null) {
			distanceMapOverlay.setImage(BitmapDescriptorFactory.fromBitmap(map));
		}
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
			StopType stopType = startNode.getLine().getDefaultStopType();
			Map<StopType, Integer> icons = App.getInstance().getStaticData().getStopTypeMiniIcons();
			Marker marker = map.addMarker(new MarkerOptions()
					.title(startNode.getName())
					.position(LocationUtils.toLatLng(startNode.getLocation()))
					.anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory.fromResource(icons.get(stopType))));
			m_markersStart.add(marker);
		}
		if (lastStartPoint != null) {
			LOG.trace("Creating marker for starting point {}", lastStartPoint);
			Marker marker = map.addMarker(new MarkerOptions()
					.title("Starting point")
					.position(lastStartPoint)
					.icon(BitmapDescriptorFactory.defaultMarker())
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
			map.addMarker(new MarkerOptions().title(String.valueOf(node.getID())).position(ll));
		}
	}

	private final class DrawAsyncTask extends SimpleAsyncTask<LatLng, DrawAsyncTask.Result, DrawAsyncTask.Result> {
		private final Set<NetworkNode> nodes;
		private final DistanceMapGeneratorConfig config;
		private final DistanceMapDrawerConfig drawConfig;

		public DrawAsyncTask(Set<NetworkNode> nodes,
				DistanceMapGeneratorConfig config, DistanceMapDrawerConfig drawConfig) {
			this.nodes = nodes;
			// Make a copy of configs to make sure any modifications are not messing with the background thread
			this.config = new DistanceMapGeneratorConfig(config);
			this.drawConfig = new DistanceMapDrawerConfig(drawConfig);
		}

		@Override protected @NonNull Result doInBackground(@Nullable LatLng location) {
			LOG.trace("Drawing map for {}", location);
			Result result = new Result();
			DistanceMapGenerator generator = new DistanceMapGenerator(nodes, config);
			Map<NetworkNode, Double> distanceMap = generator.generate(LocationUtils.fromLatLng(location));
			result.startNodes = generator.getStartNodes();
			if (isCancelled()) {
				return result;
			}
			//publishProgress(result); // looks like the color is playing catchup with the pins

			// this allocation is expensive
			DistanceMapDrawerAndroid drawer = new DistanceMapDrawerAndroid(nodes, drawConfig);
			result.distanceMap = drawer.draw(distanceMap);
			if (isCancelled()) {
				return result;
			}
			return result;
		}

		@Override protected void onProgressUpdate(Result result) {
			updateDistanceMap(result.distanceMap, result.startNodes);
		}
		@Override protected void onPostExecute(Result result) {
			updateDistanceMap(result.distanceMap, result.startNodes);
		}

		class Result {
			Collection<NetworkNode> startNodes = Collections.emptyList();
			Bitmap distanceMap = null;
		}
	}
}
