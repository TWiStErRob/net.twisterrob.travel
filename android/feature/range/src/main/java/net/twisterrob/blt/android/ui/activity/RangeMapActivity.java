package net.twisterrob.blt.android.ui.activity;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.PlaceAutocomplete;
import com.google.android.libraries.places.widget.PlaceAutocompleteActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.ClickThroughDrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import net.twisterrob.android.content.pref.ResourcePreferences;
import net.twisterrob.android.utils.concurrent.SimpleAsyncTask;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tools.BundleTools;
import net.twisterrob.android.utils.tools.StringerTools;
import net.twisterrob.android.utils.tools.ViewTools;
import net.twisterrob.android.view.MultiBottomSheetCallback;
import net.twisterrob.android.view.ShowOnlyWhenSheetNotExpandedFAB;
import net.twisterrob.android.view.layout.DoAfterLayout;
import net.twisterrob.blt.android.BuildConfig;
import net.twisterrob.blt.android.Injector;
import net.twisterrob.blt.android.data.AndroidStaticData;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.data.range.RangeMapDrawerAndroid;
import net.twisterrob.blt.android.data.range.RangeMapDrawerConfig;
import net.twisterrob.blt.android.data.range.RangeMapGenerator;
import net.twisterrob.blt.android.data.range.RangeMapGeneratorConfig;
import net.twisterrob.blt.android.data.range.TubeMapDrawer;
import net.twisterrob.blt.android.data.range.tiles.MarkerAdder;
import net.twisterrob.blt.android.data.range.tiles.TubeMapTileProvider;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.blt.android.db.model.NetworkNode;
import net.twisterrob.blt.android.feature.range.R;
import net.twisterrob.blt.android.ui.activity.main.MapActivity;
import net.twisterrob.blt.model.LineColors;
import net.twisterrob.blt.model.StopType;
import net.twisterrob.travel.map.MapUtils;

import static net.twisterrob.android.utils.tools.ResourceTools.dip;
import static net.twisterrob.android.utils.tools.ResourceTools.dipInt;

public class RangeMapActivity extends MapActivity {
	private static final Logger LOG = LoggerFactory.getLogger(RangeMapActivity.class);

	private GoogleMap map;
	private GroundOverlay mapOverlay;
	private final List<Marker> markers = new LinkedList<>();
	private BottomSheetBehavior<?> behavior;
	private RangeNearestFragment nearestFragment;
	private RangeOptionsFragment optionsFragment;
	private ClickThroughDrawerLayout drawers;
	private Set<NetworkNode> tubeNetwork;
	private @Nullable RectangularBounds locationBias;
	private DrawAsyncTask drawTask;
	private LatLng lastStartPoint;

	@Inject
	BuildConfig buildConfig;

	@Inject
	AndroidStaticData staticData;

	@Inject
	DataBaseHelper db;

	@Inject
	ResourcePreferences prefs;

	private final ActivityResultLauncher<Intent> placeAutocompleteLauncher = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			result -> processAutocompleteResult(result.getData(), result.getResultCode())
	);

	@Override protected void onCreate(Bundle savedInstanceState) {
		Injector.from(this).inject(this);
		String apiKey = getApplicationInfoWithMetadata(this).metaData.getString("com.google.android.geo.API_KEY");
		Places.initializeWithNewPlacesApiEnabled(getApplicationContext(), apiKey);

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
		optionsFragment.setConfigsUpdatedListener(() -> reDraw(lastStartPoint));
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
				View drawerContents = findViewById(R.id.view__range__drawer);
				drawers.openDrawer(((DrawerLayout.LayoutParams)drawerContents.getLayoutParams()).gravity);
			}
		});

		updateToolbarVisibility();
		@SuppressLint("StaticFieldLeak") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		@SuppressWarnings({"unused", "deprecation"}) // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		Object task = new AsyncTask<Void, Void, Set<NetworkNode>>() {
			@Override protected Set<NetworkNode> doInBackground(Void... params) {
				return db.getTubeNetwork();
			}
			@Override protected void onPostExecute(Set<NetworkNode> nodes) {
				super.onPostExecute(nodes);
				setNodes(nodes);
			}
		}.execute((Void[])null);
	}

	@Override protected void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("RangeMapActivity.lastStartPoint", lastStartPoint);
	}

	@Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		lastStartPoint = BundleTools.getParcelable(savedInstanceState, "RangeMapActivity.lastStartPoint", LatLng.class);
	}

	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/12
	private void setTranslucentStatusBar() {
		int statusBarColor = ColorUtils.setAlphaComponent(ContextCompat.getColor(this, net.twisterrob.blt.android.component.theme.R.color.accent), 0x66);
		AndroidTools.setTranslucentStatusBar(getWindow(), statusBarColor);
	}

	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/12
	private void accountForStatusBar() {
		AndroidTools.accountForStatusBar(findViewById(R.id.view__range__toolbar_container));
	}

	@Override protected void setupMap() {
		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
				.findFragmentById(net.twisterrob.blt.android.component.map.R.id.view__map);
		mapFragment.getMapAsync(new OnMapReadyCallback() {
			@Override public void onMapReady(@NonNull GoogleMap map) {
				RangeMapActivity.this.map = map;
				MapUtils.setMyLocationEnabledIfPossible(RangeMapActivity.this, map);
				View view = mapFragment.requireView();
				if (view.isLaidOut()) {
					zoomFullLondon();
				} else {
					view.getViewTreeObserver()
						.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
							@Override public void onGlobalLayout() {
								view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
								zoomFullLondon();
							}
						});
				}
				updateToolbarVisibility();
				class MapInteractorListener implements OnMapClickListener, OnMarkerClickListener, OnMapLongClickListener {
					private Marker currentMarker;
					@Override public void onMapLongClick(@NonNull LatLng latlng) {
						if (currentMarker != null) {
							currentMarker.hideInfoWindow();
							currentMarker = null;
						}
						reDraw(latlng);
					}
					@Override public void onMapClick(@NonNull LatLng latlng) {
						if (currentMarker != null) {
							currentMarker = null;
							return;
						}
						reDraw(latlng);
					}
					@Override public boolean onMarkerClick(@NonNull Marker marker) {
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
		LatLngBounds fullLondon = new LatLngBounds(
				new LatLng(51.342889, -0.611361),
				new LatLng(51.705232, 0.251388)
		);
		map.moveCamera(CameraUpdateFactory.newLatLngBounds(fullLondon, 0));
		map.moveCamera(CameraUpdateFactory.zoomIn());
	}

	public void updateToolbarVisibility() {
		boolean showToolbar = prefs.getBoolean(R.string.pref__show_toolbar, R.bool.pref__show_toolbar__default);
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

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu__action__search) {
			placeAutocompleteLauncher.launch(
					new PlaceAutocomplete.IntentBuilder()
							.setLocationBias(locationBias)
							.build(this)
			);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void processAutocompleteResult(@Nullable Intent intent, int resultCode) {
		// switch(resultCode) not possible because RESULT_* are not constants.
		if (resultCode == PlaceAutocompleteActivity.RESULT_CANCELED) {
			LOG.info("The user canceled the auto-complete operation.");
		} else if (resultCode == PlaceAutocompleteActivity.RESULT_OK) {
			AutocompletePrediction prediction = PlaceAutocomplete.getPredictionFromIntent(intent);
			AutocompleteSessionToken sessionToken = PlaceAutocomplete.getSessionTokenFromIntent(intent);
			LOG.trace("Received auto-complete result: {}, session: {}", StringerTools.toString(prediction), sessionToken);
			processAutocompleteSuccess(prediction, sessionToken);
		} else if (resultCode == PlaceAutocompleteActivity.RESULT_ERROR) {
			if (intent != null) {
				Status status = PlaceAutocomplete.getResultStatusFromIntent(intent);
				LOG.warn("Cannot search: {}", StringerTools.toString(status));
				String message = String.format(Locale.getDefault(), "Sorry, cannot search: %d/%s",
						status.getStatusCode(), status.getStatusMessage());
				Toast.makeText(this, message, Toast.LENGTH_LONG).show();
			} else {
				LOG.warn("Missing intent while processing auto-complete results, status: {}", resultCode);
			}
		} else {
			LOG.warn("Unknown result code from PlaceAutocomplete: {}", resultCode);
		}
	}
	private void processAutocompleteSuccess(
			@NonNull AutocompletePrediction prediction,
			@NonNull AutocompleteSessionToken sessionToken
	) {
		PlacesClient placesClient = Places.createClient(this);
		placesClient
				.fetchPlace(
						FetchPlaceRequest
								.builder(
										prediction.getPlaceId(),
										Collections.singletonList(Place.Field.LOCATION)
								)
								.setSessionToken(sessionToken)
								.build()
				)
				.addOnSuccessListener(this, fetchPlaceResponse -> {
					Place place = fetchPlaceResponse.getPlace();
					LOG.trace("Selected: {}", StringerTools.toString(place));
					reDraw(place.getLocation());
				})
				.addOnFailureListener(this, e -> {
					LOG.warn("Failed to fetch place details from {}, session: {}",
							StringerTools.toString(prediction), sessionToken, e);
				})
		;
	}

	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/170
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
			locationBias = null;
			return;
		}

		// TODO move to BG thread
		RangeMapDrawerAndroid rangeDrawer = new RangeMapDrawerAndroid(tubeNetwork, optionsFragment.getDrawConfig());
		// disabled for now, the bounds are hardcoded
//		map.moveCamera(CameraUpdateFactory.newLatLngBounds(rangeDrawer.getBounds(), 0));
		locationBias = RectangularBounds.newInstance(rangeDrawer.getBounds());
		// range map below
		Map<NetworkNode, Double> emptyNetwork = Collections.emptyMap();
		mapOverlay = map.addGroundOverlay(new GroundOverlayOptions()
				.positionFromBounds(rangeDrawer.getBounds())
				.transparency(0.0f)
				.image(BitmapDescriptorFactory.fromBitmap(rangeDrawer.draw(emptyNetwork)))
		);
		// tube map above
		LineColors colors = new LineColors(staticData.getLineColors());
		if (!prefs.getBoolean(R.string.pref__network_overlay, R.bool.pref__network_overlay__default)) {
			TubeMapDrawer tubeMapDrawer = new TubeMapDrawer(nodes, colors);
			tubeMapDrawer.setSize(dip(this, 1024), dip(this, 1024));
			map.addGroundOverlay(new GroundOverlayOptions()
					.positionFromBounds(rangeDrawer.getBounds())
					.transparency(0.3f)
					.image(BitmapDescriptorFactory.fromBitmap(tubeMapDrawer.draw(nodes)))
			);
		} else {
			int tileSize = Math.max(256, dipInt(this, 192));
			TubeMapTileProvider provider = new TubeMapTileProvider(nodes, colors, tileSize, buildConfig.isDebug());
			if (buildConfig.isDebug()) {
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
			if (buildConfig.isDebug()) {
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
		drawTask = new DrawAsyncTask(
				tubeNetwork,
				optionsFragment.getGenConfig(),
				optionsFragment.getDrawConfig()
		);
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
			Map<StopType, Integer> icons = staticData.getStopTypeMiniIcons();
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
		nearestFragment.updateNearestStations(startNodes, optionsFragment.getGenConfig());
		if (prefs.getBoolean(R.string.pref__show_nearest, R.bool.pref__show_nearest__default)) {
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

	private static ApplicationInfo getApplicationInfoWithMetadata(@NonNull Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			return pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			InternalError error = new InternalError("Application cannot read its own package");
			//noinspection UnnecessaryInitCause InternalError(String, Throwable) is API 24, need to split.
			error.initCause( e);
			throw error;
		}
	}

	@SuppressLint("StaticFieldLeak") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	@SuppressWarnings({"unused", "deprecation"}) // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	private final class DrawAsyncTask extends SimpleAsyncTask<LatLng, DrawAsyncTask.Result, DrawAsyncTask.Result> {
		private final @NonNull Set<NetworkNode> nodes;
		private final @NonNull RangeMapGeneratorConfig config;
		private final @NonNull RangeMapDrawerConfig drawConfig;

		public DrawAsyncTask(
				@NonNull Set<NetworkNode> nodes,
				@NonNull RangeMapGeneratorConfig config,
				@NonNull RangeMapDrawerConfig drawConfig
		) {
			this.nodes = nodes;
			this.config = config;
			this.drawConfig = drawConfig;
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
