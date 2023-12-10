package net.twisterrob.blt.android.ui.activity;

import javax.inject.Inject;

import org.slf4j.*;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.*;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import android.text.Spanned;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import net.twisterrob.android.AndroidConstants;
import net.twisterrob.android.content.HtmlParser;
import net.twisterrob.android.content.pref.ResourcePreferences;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tools.DialogTools;
import net.twisterrob.android.utils.tools.DialogTools.PopupCallbacks;
import net.twisterrob.android.utils.tools.ResourceTools;
import net.twisterrob.android.wiring.NumberPickerWidget;
import net.twisterrob.android.wiring.NumberPickerWidget.OnValueChangeListener;
import net.twisterrob.blt.android.Injector;
import net.twisterrob.blt.android.data.AndroidStaticData;
import net.twisterrob.blt.android.data.range.*;
import net.twisterrob.blt.android.feature.range.R;
import net.twisterrob.blt.android.ui.*;

public class RangeOptionsFragment extends Fragment {
	private static final Logger LOG = LoggerFactory.getLogger(RangeOptionsFragment.class);
	private NavigationView nav;
	private NumberPickerWidget walkingSpeed;
	private NumberPickerWidget interchangeTime;
	private NumberPickerWidget journeyTime;
	private NumberPickerWidget platformStreet;
	private NumberPickerWidget startWalk;
	private CompoundButton intraStation;
	private CompoundButton interStation;
	private ColorPickerWidget borderColor;
	private NumberPickerWidget borderSize;
	private CompoundButton dynamicColor;
	private NumberPickerWidget pixelDensity;
	private ColorPickerWidget distanceColor;

	interface ConfigsUpdatedListener {
		void onConfigsUpdated();
	}

	private RangeMapGeneratorConfig genConfig;
	private RangeMapDrawerConfig drawConfig;
	private ConfigsUpdatedListener configsUpdatedListener;

	@Inject
	public AndroidStaticData staticData;

	@Inject
	public ResourcePreferences prefs;

	@Override public void onAttach(@NonNull Context context) {
		Injector.from(context).inject(this);
		super.onAttach(context);
	}

	@Override public @Nullable View onCreateView(
			LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_range_options, container, false);
	}

	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		nav = (NavigationView)view.findViewById(R.id.view__range__navigation_view);
		nav.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
			@Override public boolean onNavigationItemSelected(MenuItem item) {
				return onOptionsItemSelected(item);
			}
		});
		accountForStatusBar();
		Toolbar toolbar = (Toolbar)nav.getHeaderView(0).findViewById(R.id.view__range__parameters_toolbar);
		toolbar.inflateMenu(R.menu.range_options_header);
		toolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override public boolean onMenuItemClick(MenuItem item) {
				return onOptionsItemSelected(item);
			}
		});

		bool(R.id.option__range__config__ui_show_stations, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (prefs.getBoolean(R.string.pref__show_nearest, R.bool.pref__show_nearest__default) != isChecked) {
					prefs.setBoolean(R.string.pref__show_nearest, isChecked);
				}
			}
		}).setChecked(prefs.getBoolean(R.string.pref__show_nearest, R.bool.pref__show_nearest__default));
		bool(R.id.option__range__config__ui_show_toolbar, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (prefs.getBoolean(R.string.pref__show_toolbar, R.bool.pref__show_toolbar__default) != isChecked) {
					prefs.setBoolean(R.string.pref__show_toolbar, isChecked);
					((RangeMapActivity)getActivity()).updateToolbarVisibility();
				}
			}
		}).setChecked(prefs.getBoolean(R.string.pref__show_toolbar, R.bool.pref__show_toolbar__default));
		bool(R.id.option__range__config__ui_network_overlay, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (prefs.getBoolean(R.string.pref__network_overlay, R.bool.pref__network_overlay__default) != isChecked) {
					prefs.setBoolean(R.string.pref__network_overlay, isChecked);
				}
			}
		}).setChecked(prefs.getBoolean(R.string.pref__network_overlay, R.bool.pref__network_overlay__default));

		intraStation = bool(R.id.option__range__config__interchange_intrastation, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (genConfig.isAllowIntraStationInterchange() != isChecked) {
					genConfig.setAllowIntraStationInterchange(isChecked);
					configsUpdatedListener.onConfigsUpdated();
				}
			}
		});
		interStation = bool(R.id.option__range__config__interchange_interstation, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (genConfig.isAllowInterStationInterchange() != isChecked) {
					genConfig.setAllowInterStationInterchange(isChecked);
					configsUpdatedListener.onConfigsUpdated();
				}
			}
		});
		walkingSpeed = number(R.id.option__range__config__walking_speed,
				RangeMapGeneratorConfig.SPEED_ON_FOOT_MIN,
				RangeMapGeneratorConfig.SPEED_ON_FOOT_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getWalkingSpeed() != newVal) {
							genConfig.setWalkingSpeed(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		interchangeTime = number(R.id.option__range__config__interchange_time,
				RangeMapGeneratorConfig.TIME_TRANSFER_MIN,
				RangeMapGeneratorConfig.TIME_TRANSFER_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getIntraStationInterchangeTime() != newVal) {
							genConfig.setIntraStationInterchangeTime(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		journeyTime = number(R.id.option__range__config__journey_time,
				RangeMapGeneratorConfig.MINUTES_MIN,
				RangeMapGeneratorConfig.MINUTES_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getTotalAllottedTime() != newVal) {
							genConfig.setTotalAllottedTime(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		platformStreet = number(R.id.option__range__config__transfer_entry_exit,
				RangeMapGeneratorConfig.TIME_PLATFORM_TO_STREET_MIN,
				RangeMapGeneratorConfig.TIME_PLATFORM_TO_STREET_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getPlatformToStreetTime() != newVal) {
							genConfig.setPlatformToStreetTime(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		startWalk = number(R.id.option__range__config__journey_start,
				RangeMapGeneratorConfig.START_WALK_MIN,
				RangeMapGeneratorConfig.START_WALK_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (genConfig.getInitialAllottedWalkTime() != newVal) {
							genConfig.setInitialAllottedWalkTime(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});

		dynamicColor = bool(R.id.option__range__config__dynamic_color, new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (drawConfig.isDynamicColor() != isChecked) {
					drawConfig.setDynamicColor(isChecked);
					configsUpdatedListener.onConfigsUpdated();
				}
			}
		});
		borderSize = number(R.id.option__range__config__border_size,
				RangeMapDrawerConfig.BORDER_SIZE_MIN,
				RangeMapDrawerConfig.BORDER_SIZE_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (drawConfig.getBorderSize() != newVal) {
							drawConfig.setBorderSize((int)newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		borderColor = color(R.id.option__range__config__border_color,
				new ColorPickerWidget.OnValueChangeListener() {
					@Override public void onValueChange(
							ColorPickerWidget picker, @ColorInt int oldVal, @ColorInt int newVal) {
						if (drawConfig.getBorderColor() != newVal) {
							drawConfig.setBorderColor(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		distanceColor = color(R.id.option__range__config__range_color,
				new ColorPickerWidget.OnValueChangeListener() {
					@Override public void onValueChange(
							ColorPickerWidget picker, @ColorInt int oldVal, @ColorInt int newVal) {
						if (drawConfig.getRangeColor() != newVal) {
							drawConfig.setRangeColor(newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
		pixelDensity = number(R.id.option__range__config__pixel_density,
				RangeMapDrawerConfig.PIXEL_DENSITY_MIN,
				RangeMapDrawerConfig.PIXEL_DENSITY_MAX,
				new OnValueChangeListener() {
					@Override public void onValueChange(NumberPickerWidget picker, float oldVal, float newVal) {
						if (drawConfig.getPixelDensity() != newVal) {
							drawConfig.setPixelDensity((int)newVal);
							configsUpdatedListener.onConfigsUpdated();
						}
					}
				});
	}

	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/12
	private void accountForStatusBar() {
		AndroidTools.accountForStatusBar(nav.getHeaderView(0));
	}

	@SuppressWarnings("unchecked")
	private <T extends View> T getAction(@IdRes int menuItemId) {
		MenuItem switchItem = nav.getMenu().findItem(menuItemId);
		return (T)switchItem.getActionView();
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

	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/21
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getGroupId() == R.id.group__range__config__generator
				|| item.getGroupId() == R.id.group__range__config__draw
				|| item.getGroupId() == R.id.group__range__config__ui) {
			@StringRes int tooltipID = getTooltip(item);
			TubeHtmlHandler tagHandler = new TubeHtmlHandler(getContext(), staticData);
			Spanned tooltip = HtmlParser.fromHtml(getString(tooltipID), null, tagHandler);
			DialogTools
					.notify(getContext(), PopupCallbacks.DoNothing.<Boolean>instance())
					.setTitle(item.getTitle())
					.setMessage(tooltip)
					.show();
			return true;
		}
		int id = item.getItemId();
		if (id == R.id.menu__action__range__reset_generator) {
			genConfig.set(new RangeMapGeneratorConfig());
			bindConfigs(genConfig, drawConfig); // update self UI
			configsUpdatedListener.onConfigsUpdated();
			return true;
		} else if (id == R.id.menu__action__range__reset_drawing) {
			drawConfig.set(new RangeMapDrawerConfig(staticData.getLineColors()));
			bindConfigs(genConfig, drawConfig); // update self UI
			configsUpdatedListener.onConfigsUpdated();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private int getTooltip(MenuItem item) {
		int id = AndroidConstants.INVALID_RESOURCE_ID;
		String name = getResources().getResourceEntryName(item.getItemId());
		if (name != null) {
			name = name.substring("option$".length()) + "$tooltip";
			id = ResourceTools.getStringResourceID(getContext(), name);
		}
		if (id == AndroidConstants.INVALID_RESOURCE_ID) {
			id = R.string.range__config__missing_tooltip;
		}
		return id;
	}

	public void bindConfigs(RangeMapGeneratorConfig genConfig, RangeMapDrawerConfig drawConfig) {
		this.genConfig = genConfig;
		this.drawConfig = drawConfig;
		interStation.setChecked(genConfig.isAllowInterStationInterchange());
		intraStation.setChecked(genConfig.isAllowIntraStationInterchange());
		journeyTime.setValue(genConfig.getTotalAllottedTime());
		startWalk.setValue(genConfig.getInitialAllottedWalkTime());
		interchangeTime.setValue(genConfig.getIntraStationInterchangeTime());
		walkingSpeed.setValue(genConfig.getWalkingSpeed());
		platformStreet.setValue(genConfig.getPlatformToStreetTime());
		borderColor.setValue(drawConfig.getBorderColor());
		borderSize.setValue(drawConfig.getBorderSize());
		dynamicColor.setChecked(drawConfig.isDynamicColor());
		pixelDensity.setValue(drawConfig.getPixelDensity());
		distanceColor.setValue(drawConfig.getRangeColor());
	}

	public void setConfigsUpdatedListener(ConfigsUpdatedListener configsUpdatedListener) {
		this.configsUpdatedListener = configsUpdatedListener;
	}
}
