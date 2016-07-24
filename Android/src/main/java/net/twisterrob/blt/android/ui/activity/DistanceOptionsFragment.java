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
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.text.Spanned;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import net.twisterrob.android.content.HtmlParser;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tools.AndroidTools.PopupCallbacks;
import net.twisterrob.android.wiring.NumberPickerWidget;
import net.twisterrob.android.wiring.NumberPickerWidget.OnValueChangeListener;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.ui.*;

public class DistanceOptionsFragment extends Fragment {
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
	private ColorPickerWidget borderColor;
	private NumberPickerWidget borderSize;
	private CompoundButton dynamicColor;
	private NumberPickerWidget pixelDensity;
	private ColorPickerWidget distanceColor;

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
		nav.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
			@Override public boolean onNavigationItemSelected(MenuItem item) {
				return onOptionsItemSelected(item);
			}
		});
		AndroidTools.accountForStatusBar(nav.getHeaderView(0));
		Toolbar toolbar = (Toolbar)nav.getHeaderView(0).findViewById(R.id.parameters_toolbar);
		toolbar.inflateMenu(R.menu.drawer_distance_options_header);
		toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override public boolean onMenuItemClick(MenuItem item) {
				return onOptionsItemSelected(item);
			}
		});

		bool(R.id.distance_ui_show_stations, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (prefs.getBoolean("showNearest", true) != isChecked) {
					prefs.edit().putBoolean("showNearest", isChecked).apply();
				}
			}
		}).setChecked(prefs.getBoolean("showNearest", true));
		bool(R.id.distance_ui_show_toolbar, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (prefs.getBoolean("showToolbar", false) != isChecked) {
					prefs.edit().putBoolean("showToolbar", isChecked).apply();
					((DistanceMapActivity)getActivity()).updateToolbarVisibility();
				}
			}
		}).setChecked(prefs.getBoolean("showToolbar", false));

		intraStation = bool(R.id.distance_config_interchange_intrastation, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (genConfig.allowsIntraStationInterchange() != isChecked) {
					genConfig.setIntraStationInterchange(isChecked);
					configsUpdatedListener.onConfigsUpdated();
				}
			}
		});
		interStation = bool(R.id.distance_config_interchange_interstation, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (genConfig.allowsInterStationInterchange() != isChecked) {
					genConfig.setInterStationInterchange(isChecked);
					configsUpdatedListener.onConfigsUpdated();
				}
			}
		});
		walkingSpeed = number(R.id.distance_config_walking_speed,
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
		interchangeTime = number(R.id.distance_config_interchange_time,
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
		journeyTime = number(R.id.distance_config_journey_time,
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
		platformStreet = number(R.id.distance_config_transfer_entry_exit,
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
		startWalk = number(R.id.distance_config_journey_start,
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

		dynamicColor = bool(R.id.distance_config_dynamic_color, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (drawConfig.isDynamicColor() != isChecked) {
					drawConfig.setDynamicColor(isChecked);
					configsUpdatedListener.onConfigsUpdated();
				}
			}
		});
		borderSize = number(R.id.distance_config_border_size,
				DistanceMapDrawerConfig.BORDER_SIZE_MIN,
				DistanceMapDrawerConfig.BORDER_SIZE_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (drawConfig.getBorderSize() != newVal) {
							drawConfig.setBorderSize((int)newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		borderColor = color(R.id.distance_config_border_color,
				new ColorPickerWidget.OnValueChangeListener() {
					@Override public void onValueChange(
							ColorPickerWidget picker, @ColorInt int oldVal, @ColorInt int newVal) {
						if (drawConfig.getBorderColor() != newVal) {
							drawConfig.setBorderColor(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		distanceColor = color(R.id.distance_config_distance_color,
				new ColorPickerWidget.OnValueChangeListener() {
					@Override public void onValueChange(
							ColorPickerWidget picker, @ColorInt int oldVal, @ColorInt int newVal) {
						if (drawConfig.getDistanceColor() != newVal) {
							drawConfig.setDistanceColor(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		pixelDensity = number(R.id.distance_config_pixel_density,
				DistanceMapDrawerConfig.PIXEL_DENSITY_MIN,
				DistanceMapDrawerConfig.PIXEL_DENSITY_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (drawConfig.getPixelDensity() != newVal) {
							drawConfig.setPixelDensity((int)newVal);
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

	private CompoundButton bool(@IdRes int menuItemId, OnCheckedChangeListener listener) {
		CompoundButton check = getAction(menuItemId);
		check.setOnCheckedChangeListener(listener);
		return check;
	}

	private NumberPickerWidget number(@IdRes int menuItemId,
			float min, float max, NumberPickerWidget.OnValueChangeListener listener) {
		NumberPickerWidget picker = new NumberPickerWidget(getAction(menuItemId));
		picker.setMinValue(min);
		picker.setMaxValue(max);
		picker.setOnValueChangedListener(listener);
		return picker;
	}

	private ColorPickerWidget color(@IdRes int menuItemId,
			ColorPickerWidget.OnValueChangeListener listener) {
		ColorPickerWidget picker = new ColorPickerWidget(getAction(menuItemId));
		picker.setOnValueChangedListener(listener);
		return picker;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getGroupId() == R.id.menu$group$distance_config_generate
				|| item.getGroupId() == R.id.menu$group$distance_config_draw
				|| item.getGroupId() == R.id.menu$group$distance_config_ui) {
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
		}
		switch (item.getItemId()) {
			case R.id.menu$option$distance_reset_distance:
				genConfig.set(new DistanceMapGeneratorConfig());
				bindConfigs(genConfig, drawConfig); // update self UI
				configsUpdatedListener.onConfigsUpdated();
				return true;
			case R.id.menu$option$distance_reset_drawing:
				drawConfig.set(new DistanceMapDrawerConfig());
				bindConfigs(genConfig, drawConfig); // update self UI
				configsUpdatedListener.onConfigsUpdated();
				return true;
		}
		return super.onOptionsItemSelected(item);
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
		borderColor.setValue(drawConfig.getBorderColor());
		borderSize.setValue(drawConfig.getBorderSize());
		dynamicColor.setChecked(drawConfig.isDynamicColor());
		pixelDensity.setValue(drawConfig.getPixelDensity());
		distanceColor.setValue(drawConfig.getDistanceColor());
	}

	public void setConfigsUpdatedListener(ConfigsUpdatedListener configsUpdatedListener) {
		this.configsUpdatedListener = configsUpdatedListener;
	}
}
