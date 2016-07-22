package net.twisterrob.blt.android.ui.activity;

import org.slf4j.*;

import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.*;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.text.Spanned;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import net.twisterrob.android.content.HtmlParser;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tools.AndroidTools.PopupCallbacks;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.ui.*;
import net.twisterrob.blt.android.ui.NumberPickerWidget.OnValueChangeListener;

public class DistanceOptionsFragment extends Fragment implements OnNavigationItemSelectedListener {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceOptionsFragment.class);
	private NavigationView nav;
	private NumberPickerWidget walkingSpeed;
	private NumberPickerWidget interchangeTime;
	private NumberPickerWidget journeyTime;
	private NumberPickerWidget platformStreet;
	private NumberPickerWidget startWalk;
	private CompoundButton intraStation;
	private CompoundButton interStation;
	private SharedPreferences prefs;

	interface ConfigsUpdatedListener {
		void onConfigsUpdated();
	}

	private DistanceMapGeneratorConfig genConfig;
	private DistanceMapDrawerConfig drawConfig;
	private ConfigsUpdatedListener configsUpdatedListener;

	@Override public void onAttach(Context context) {
		super.onAttach(context);
		prefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
	}

	@Override public @Nullable View onCreateView(
			LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_distance_options, container, false);
	}

	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		nav = (NavigationView)view.findViewById(R.id.navigation_view);
		nav.setNavigationItemSelectedListener(this);

		CompoundButton showNearestStations = getAction(R.id.distance_ui_show_stations);
		showNearestStations.setChecked(prefs.getBoolean("showNearest", true));
		showNearestStations.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (prefs.getBoolean("showNearest", true) != isChecked) {
					prefs.edit().putBoolean("showNearest", isChecked).apply();
				}
			}
		});
		intraStation = getAction(R.id.distance_config_interchange_intrastation);
		intraStation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (genConfig.allowsIntraStationInterchange() != isChecked) {
					genConfig.setIntraStationInterchange(isChecked);
					configsUpdatedListener.onConfigsUpdated();
				}
			}
		});
		interStation = getAction(R.id.distance_config_interchange_interstation);
		interStation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (genConfig.allowsInterStationInterchange() != isChecked) {
					genConfig.setInterStationInterchange(isChecked);
					configsUpdatedListener.onConfigsUpdated();
				}
			}
		});
		walkingSpeed = picker(R.id.distance_config_walking_speed,
				DistanceMapGeneratorConfig.SPEED_ON_FOOT_MIN,
				DistanceMapGeneratorConfig.SPEED_ON_FOOT_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getWalkingSpeed() != newVal) {
							genConfig.setWalkingSpeed(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		interchangeTime = picker(R.id.distance_config_interchange_time,
				DistanceMapGeneratorConfig.TIME_TRANSFER_MIN,
				DistanceMapGeneratorConfig.TIME_TRANSFER_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getIntraStationInterchangeTime() != newVal) {
							genConfig.setIntraStationInterchangeTime(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		journeyTime = picker(R.id.distance_config_journey_time,
				DistanceMapGeneratorConfig.MINUTES_MIN,
				DistanceMapGeneratorConfig.MINUTES_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getTotalAllottedTime() != newVal) {
							genConfig.setTotalAllottedTime(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		platformStreet = picker(R.id.distance_config_transfer_entry_exit,
				DistanceMapGeneratorConfig.TIME_PLATFORM_TO_STREET_MIN,
				DistanceMapGeneratorConfig.TIME_PLATFORM_TO_STREET_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getTotalAllottedTime() != newVal) {
							genConfig.setTotalAllottedTime(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		startWalk = picker(R.id.distance_config_journey_start,
				DistanceMapGeneratorConfig.START_WALK_MIN,
				DistanceMapGeneratorConfig.START_WALK_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getTotalAllottedTime() != newVal) {
							genConfig.setTotalAllottedTime(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
	}

	@SuppressWarnings("unchecked")
	private <T extends View> T getAction(@IdRes int menuItemId) {
		MenuItem switchItem = nav.getMenu().findItem(menuItemId);
		return (T)MenuItemCompat.getActionView(switchItem);
	}

	private NumberPickerWidget picker(@IdRes int menuItemId, float min, float max, OnValueChangeListener listener) {
		NumberPickerWidget picker = new NumberPickerWidget(getAction(menuItemId));
		picker.setMinValue(min);
		picker.setMaxValue(max);
		picker.setOnValueChangedListener(listener);
		return picker;
	}

	@Override public boolean onNavigationItemSelected(MenuItem item) {
		String name = getResources().getResourceEntryName(item.getItemId()) + "_tooltip";
		@StringRes int tooltipID = AndroidTools.getStringResourceID(getContext(), name);
		if (tooltipID != AndroidTools.INVALID_RESOURCE_ID) {
			Spanned tooltip = HtmlParser.fromHtml(getString(tooltipID), null, new TubeHtmlHandler(getContext()));
			AndroidTools
					.notify(getContext(), PopupCallbacks.DoNothing.<Boolean>instance())
					.setTitle(item.getTitle())
					.setMessage(tooltip)
					.show();
			return true;
		}
		return false;
	}

	public void bindConfigs(DistanceMapGeneratorConfig genConfig, DistanceMapDrawerConfig drawConfig) {
		this.genConfig = genConfig;
		this.drawConfig = drawConfig;
		interStation.setChecked(genConfig.allowsInterStationInterchange());
		intraStation.setChecked(genConfig.allowsIntraStationInterchange());
		journeyTime.setValue(genConfig.getTotalAllottedTime());
		startWalk.setValue(genConfig.getInitialAllottedWalkTime());
		interchangeTime.setValue(genConfig.getIntraStationInterchangeTime());
		walkingSpeed.setValue(genConfig.getWalkingSpeed());
		platformStreet.setValue(genConfig.getPlatformToStreetTime());
	}

	public void setConfigsUpdatedListener(ConfigsUpdatedListener configsUpdatedListener) {
		this.configsUpdatedListener = configsUpdatedListener;
	}
}
