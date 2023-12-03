package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import org.slf4j.*;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.os.*;
import androidx.annotation.*;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.*;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.Marker;

import net.twisterrob.android.utils.concurrent.SimpleAsyncTask;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tools.StringerTools;
import net.twisterrob.android.utils.tools.ViewTools;
import net.twisterrob.android.view.*;
import net.twisterrob.android.view.layout.DoAfterLayout;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.data.range.*;
import net.twisterrob.blt.android.data.range.tiles.*;
import net.twisterrob.blt.android.db.model.NetworkNode;
import net.twisterrob.blt.android.ui.activity.RangeOptionsFragment.ConfigsUpdatedListener;
import net.twisterrob.blt.android.ui.activity.main.MapActivity;
import net.twisterrob.blt.model.StopType;

import static net.twisterrob.android.utils.tools.ResourceTools.*;

public class RangeMapActivity extends MapActivity {
	private static final Logger LOG = LoggerFactory.getLogger(RangeMapActivity.class);

	private GoogleMap map;
	private GroundOverlay mapOverlay;
	private List<Marker> markers = new LinkedList<>();
	private RangeMapGeneratorConfig genConfig = new RangeMapGeneratorConfig();
	private RangeMapDrawerConfig drawConfig = new RangeMapDrawerConfig();
	private BottomSheetBehavior<?> behavior;
	private RangeNearestFragment nearestFragment;
	private RangeOptionsFragment optionsFragment;
	private ClickThroughDrawerLayout drawers;
	private Set<NetworkNode> tubeNetwork;
	private DrawAsyncTask drawTask;
	private LatLng lastStartPoint;
	@SuppressWarnings("deprecation")
	private SupportPlaceAutocompleteFragment searchFragment;

	@Override protected void onCreate(Bundle savedInstanceState) {
		setTranslucentStatusBar();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_range_map);

		accountForStatusBar();
		Toolbar toolbar = (Toolbar)findViewById(R.id.view__range__toolbar);
		setSupportActionBar(toolbar);
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_launcher);
		drawers = (ClickThroughDrawerLayout)findViewById(R.id.view__range__drawer_layout);
		new DoAfterLayout(drawers, true) {
			@Override protected void onLayout(@NonNull View view) {
				// when the map has more than 40% of the screen, allow using it even when the drawer is open
				if (optionsFragment.getView().getWidth() <= drawers.getWidth() * 0.60f) {
					drawers.setAllowClickThrough(true);
				}
			}
		};

		FragmentManager fm = getSupportFragmentManager();
		nearestFragment = (RangeNearestFragment)fm.findFragmentById(R.id.view__range__bottom_sheet);
		optionsFragment = (RangeOptionsFragment)fm.findFragmentById(R.id.view__range__drawer);
		optionsFragment.bindConfigs(genConfig, drawConfig);
		optionsFragment.setConfigsUpdatedListener(new ConfigsUpdatedListener() {
			@Override public void onConfigsUpdated() {
				reDraw(lastStartPoint);
			}
		});
		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.view__range__fab);
		behavior = BottomSheetBehavior.from(findViewById(R.id.view__range__bottom_sheet));
		behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		behavior.addBottomSheetCallback(new MultiBottomSheetCallback.Builder()
				//.add(new LoggingBottomSheetCallback())
				.add(new ShowOnlyWhenSheetNotExpandedFAB(fab))
				//.add(new BottomMarginAdjuster(new SupportFragmentViewProvider(mapFragment), false))
				.build());
		fab.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				//noinspection WrongConstant it's not a constant, and it is a gravity field
				drawers.openDrawer(((DrawerLayout.LayoutParams)optionsFragment.getView().getLayoutParams()).gravity);
			}
		});

		setupSearch(fm.findFragmentById(R.id.view__range__search));

		updateToolbarVisibility();
		@SuppressLint("StaticFieldLeak") // https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		@SuppressWarnings({"unused", "deprecation"}) // https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		Object task = new AsyncTask<Void, Void, Set<NetworkNode>>() {
			@Override protected Set<NetworkNode> doInBackground(Void... params) {
				return App.db().getTubeNetwork();
			}
			@Override protected void onPostExecute(Set<NetworkNode> nodes) {
				super.onPostExecute(nodes);
				setNodes(nodes);
			}
		}.execute((Void[])null);
	}

	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/12
	private void setTranslucentStatusBar() {
		int statusBarColor = ColorUtils.setAlphaComponent(ContextCompat.getColor(this, R.color.accent), 0x66);
		AndroidTools.setTranslucentStatusBar(getWindow(), statusBarColor);
	}

	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/12
	private void accountForStatusBar() {
		AndroidTools.accountForStatusBar(findViewById(R.id.view__range__toolbar_container));
	}

	@SuppressWarnings("deprecation")
	private void setupSearch(Fragment searchFragment) {
		this.searchFragment = (SupportPlaceAutocompleteFragment)searchFragment;
		this.searchFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
			@Override public void onPlaceSelected(Place place) {
				LOG.trace("Selected: {}", StringerTools.toString(place));
				reDraw(place.getLatLng());
			}

			@Override public void onError(Status status) {
				LOG.warn("Cannot search: {}", StringerTools.toString(status));
				String message = String.format(Locale.getDefault(), "Sorry, cannot search: %d/%s",
						status.getStatusCode(), status.getStatusMessage());
				Toast.makeText(RangeMapActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override protected void setupMap() {
		SupportMapFragment mapFragment =
				(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.view__map);
		mapFragment.getMapAsync(new OnMapReadyCallback() {
			@Override public void onMapReady(GoogleMap map) {
				RangeMapActivity.this.map = map;
				map.setMyLocationEnabled(true);
				zoomFullLondon();
				updateToolbarVisibility();
				class MapInteractorListener
						implements OnMapClickListener, OnMarkerClickListener, OnMapLongClickListener {
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
				setNodes(tubeNetwork);
			}
		});
	}

	private void zoomFullLondon() {
		LatLngBounds fullLondon = new LatLngBounds(new LatLng(51.342889, -0.611361), new LatLng(51.705232, 0.251388));
		Point screen = AndroidTools.getScreenSize(ContextCompat.getDisplayOrDefault(this));
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(fullLondon, screen.x, screen.y, dipInt(this, -160)));
		// TODO below doesn't work, even with latest GMS, negative padding above is a workaround
//		map.moveCamera(CameraUpdateFactory.zoomIn());
	}

	public void updateToolbarVisibility() {
		boolean showToolbar = App.prefs().getBoolean(R.string.pref__show_toolbar, R.bool.pref__show_toolbar__default);
		final View container = findViewById(R.id.view__range__toolbar_container);
		ViewTools.displayedIf(container, showToolbar);
		new DoAfterLayout(drawers, true) {
			@Override protected void onLayout(@NonNull View view) {
				if (map == null) {
					return; // too early, just ignore, map-ready will call this too
				}
				// CONSIDER rewriting this as a behavior if possible (could account for expanding toolbar and bottom sheet)
				// https://medium.com/google-developers/intercepting-everything-with-coordinatorlayout-behaviors-8c6adc140c26
				UiSettings ui = map.getUiSettings();
				int topMargin = 0;
				if (ui.isMyLocationButtonEnabled() || ui.isCompassEnabled()) {
					View bottomMostTopView = findViewById(R.id.view__range__toolbar_container);
					if (bottomMostTopView.getVisibility() != View.GONE) {
						topMargin = bottomMostTopView.getBottom() + ViewTools.getBottomMargin(bottomMostTopView);
					} else {
						@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/12
						int height = AndroidTools.getStatusBarHeight(RangeMapActivity.this);
						topMargin = height;
					}
				}
				int bottomMargin = 0;
				if (ui.isMapToolbarEnabled() || ui.isZoomControlsEnabled()/* || ui.isIndoorLevelPickerEnabled()*/) {
					View topMostBottomView = findViewById(R.id.view__range__fab);
					if (topMostBottomView.getVisibility() != View.GONE) {
						bottomMargin = topMostBottomView.getTop() - ViewTools.getTopMargin(topMostBottomView);
						bottomMargin = ((View)topMostBottomView.getParent()).getHeight() - bottomMargin;
					}
				}
				map.setPadding(0 /*left*/, topMargin, 0 /*right*/, bottomMargin);
			}
		};
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.range_map, menu);
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
			drawTask.cancel();
			drawTask = null;
		}
	}

	private void setNodes(Set<NetworkNode> nodes) {
		LOG.trace("setNodes(nodes:{})", nodes != null? nodes.size() : null);
		tubeNetwork = nodes;
		if (tubeNetwork == null || map == null) {
			return;
		}

		// TODO move to BG thread
		RangeMapDrawerAndroid rangeDrawer = new RangeMapDrawerAndroid(tubeNetwork, drawConfig);
		// disabled for now, the bounds are hardcoded
//		map.moveCamera(CameraUpdateFactory.newLatLngBounds(rangeDrawer.getBounds(), 0));
		searchFragment.setBoundsBias(rangeDrawer.getBounds());
		// range map below
		Map<NetworkNode, Double> emptyNetwork = Collections.emptyMap();
		mapOverlay = map.addGroundOverlay(new GroundOverlayOptions()
				.positionFromBounds(rangeDrawer.getBounds())
				.transparency(0.0f)
				.image(BitmapDescriptorFactory.fromBitmap(rangeDrawer.draw(emptyNetwork)))
		);
//		// tube map above
		if (!App.prefs().getBoolean(R.string.pref__network_overlay, R.bool.pref__network_overlay__default)) {
			TubeMapDrawer tubeMapDrawer = new TubeMapDrawer(nodes);
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			tubeMapDrawer.setSize(dip(this, 1024), dip(this, 1024));
			map.addGroundOverlay(new GroundOverlayOptions()
					.positionFromBounds(rangeDrawer.getBounds())
					.transparency(0.3f)
					.image(BitmapDescriptorFactory.fromBitmap(tubeMapDrawer.draw(nodes)))
			);
		} else {
			TubeMapTileProvider provider = new TubeMapTileProvider(nodes, Math.max(256, dipInt(this, 192)));
			if (BuildConfig.DEBUG) {
				provider.setMarkers(new MarkerAdder() {
					@Override public void addMarker(final double lat, final double lon, final String text) {
						getWindow().getDecorView().post(new Runnable() {
							@Override public void run() {
								map.addMarker(new MarkerOptions()
										.icon(BitmapDescriptorFactory.defaultMarker())
										.title(text)
										.position(new LatLng(lat, lon))
								);
							}
						});
					}
				});
			}
			TileOverlay overlay = map.addTileOverlay(new TileOverlayOptions()
					.tileProvider(provider)
					.transparency(0.50f)
			);
			if (BuildConfig.DEBUG) {
				overlay.clearTileCache();
			}
		}
		if (lastStartPoint != null) {
			reDraw(lastStartPoint);
		}
	}

	private void reDraw(LatLng latlng) {
		lastStartPoint = latlng;
		if (map == null || latlng == null) {
			return;
		}
		nearestFragment.updateLocation(latlng, null);

		LOG.trace("reDraw({}) / task: {}", latlng, StringerTools.toString(drawTask));
		killTask();
		if (tubeNetwork == null) {
			String message = "Someone has quick fingers, Tube network is not ready, please wait and try again.";
			Toast.makeText(RangeMapActivity.this, message, Toast.LENGTH_LONG).show();
			return;
		}
		// don't set empty image for the ground overlay here because it flashes
		drawTask = new DrawAsyncTask(tubeNetwork, genConfig, drawConfig);
		AndroidTools.executePreferParallel(drawTask, latlng);
	}

	private void updateMap(Bitmap map, Collection<NetworkNode> startNodes) {
		LOG.trace("updateDistanceMap({})", map);
		if (map != null) {
			mapOverlay.setImage(BitmapDescriptorFactory.fromBitmap(map));
		}
		reCreateMarkers(startNodes);
		updateNearestStations(startNodes);
	}

	private void reCreateMarkers(Collection<NetworkNode> startNodes) {
		LOG.trace("reCreateMarkers");
		for (Marker marker : markers) {
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
			markers.add(marker);
		}
		if (lastStartPoint != null) {
			LOG.trace("Creating marker for starting point {}", lastStartPoint);
			Marker marker = map.addMarker(new MarkerOptions()
					.title("Starting point")
					.position(lastStartPoint)
					.icon(BitmapDescriptorFactory.defaultMarker())
			);
			markers.add(marker);
		}
	}

	private void updateNearestStations(Collection<NetworkNode> startNodes) {
		nearestFragment.updateNearestStations(startNodes, genConfig);
		if (App.prefs().getBoolean(R.string.pref__show_nearest, R.bool.pref__show_nearest__default)) {
			behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		}
	}

	@SuppressWarnings("unused")
	private void addMarkers(Iterable<NetworkNode> nodes) {
		for (NetworkNode node : nodes) {
			LatLng ll = LocationUtils.toLatLng(node.getLocation());
			map.addMarker(new MarkerOptions().title(String.valueOf(node.getID())).position(ll));
		}
	}

	@SuppressLint("StaticFieldLeak") // https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	@SuppressWarnings({"unused", "deprecation"}) // https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	private final class DrawAsyncTask extends SimpleAsyncTask<LatLng, DrawAsyncTask.Result, DrawAsyncTask.Result> {
		private final Set<NetworkNode> nodes;
		private final RangeMapGeneratorConfig config;
		private final RangeMapDrawerConfig drawConfig;

		public DrawAsyncTask(Set<NetworkNode> nodes,
				RangeMapGeneratorConfig config, RangeMapDrawerConfig drawConfig) {
			this.nodes = nodes;
			// Make a copy of configs to make sure any modifications are not messing with the background thread
			this.config = new RangeMapGeneratorConfig(config);
			this.drawConfig = new RangeMapDrawerConfig(drawConfig);
		}

		@Override protected @NonNull Result doInBackground(@Nullable LatLng location) {
			LOG.trace("Drawing map for {}", location);
			Result result = new Result();
			RangeMapGenerator generator = new RangeMapGenerator(nodes, config);
			Map<NetworkNode, Double> ranges = generator.generate(LocationUtils.fromLatLng(location));
			result.startNodes = generator.getStartNodes();
			if (isCancelled()) {
				return result;
			}
			//publishProgress(result); // looks like the color is playing catchup with the pins

			// this allocation is expensive
			RangeMapDrawerAndroid drawer = new RangeMapDrawerAndroid(nodes, drawConfig);
			result.overlay = drawer.draw(ranges);
			if (isCancelled()) {
				return result;
			}
			return result;
		}

		@Override protected void onProgressUpdate(Result result) {
			updateMap(result.overlay, result.startNodes);
		}
		@Override protected void onPostExecute(Result result) {
			updateMap(result.overlay, result.startNodes);
		}

		public void cancel() {
			super.cancel(true);
		}

		class Result {
			Collection<NetworkNode> startNodes = Collections.emptyList();
			Bitmap overlay = null;
		}
	}
}
