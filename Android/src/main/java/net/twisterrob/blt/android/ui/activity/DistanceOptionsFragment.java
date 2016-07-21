package net.twisterrob.blt.android.ui.activity;

import org.slf4j.*;

import android.os.Bundle;
import android.support.annotation.*;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Spanned;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import net.twisterrob.android.content.HtmlParser;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tools.AndroidTools.PopupCallbacks;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.ui.TubeHtmlHandler;

public class DistanceOptionsFragment extends Fragment implements OnNavigationItemSelectedListener {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceOptionsFragment.class);
	private NavigationView nav;

	interface ConfigsUpdatedListener {
		void onConfigsUpdated();
	}

	private DistanceMapGeneratorConfig genConfig;
	private DistanceMapDrawerConfig drawConfig;
	private ConfigsUpdatedListener configsUpdatedListener;

	@Override public @Nullable View onCreateView(
			LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.inc_distance_options, container, false);
	}
	@Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		nav = (NavigationView)view.findViewById(R.id.navigation_view);
		nav.setNavigationItemSelectedListener(this);
		MenuItem switchItem = nav.getMenu().findItem(R.id.distance_config_interchange_intrastation);
		CompoundButton switchView = (CompoundButton)MenuItemCompat.getActionView(switchItem);
		switchView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				LOG.debug("Checked {}", isChecked);
			}
		});
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
	}
	public void setConfigsUpdatedListener(ConfigsUpdatedListener configsUpdatedListener) {
		this.configsUpdatedListener = configsUpdatedListener;
	}
	public void show() {
		DrawerLayout parent = (DrawerLayout)nav.getParent();
		//noinspection WrongConstant it's not a constant, and it is a gravity field
		parent.openDrawer(((DrawerLayout.LayoutParams)nav.getLayoutParams()).gravity);
	}
}
