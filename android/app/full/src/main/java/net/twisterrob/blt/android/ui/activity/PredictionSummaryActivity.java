package net.twisterrob.blt.android.ui.activity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.android.utils.tools.BundleTools;
import net.twisterrob.android.utils.tools.IntentTools;
import net.twisterrob.blt.android.app.full.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.ListViewHandler;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.trackernet.PredictionSummaryFeed;
import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData;
import net.twisterrob.blt.io.feeds.trackernet.model.Platform;
import net.twisterrob.blt.io.feeds.trackernet.model.Station;
import net.twisterrob.blt.io.feeds.trackernet.model.Train;
import net.twisterrob.blt.model.Line;
import net.twisterrob.blt.model.PlatformDirection;
import net.twisterrob.java.utils.CollectionTools;

/**
 * http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf
 */
public class PredictionSummaryActivity extends BaseActivity implements
		OnGroupExpandListener,
		OnGroupCollapseListener {
	public static final String EXTRA_LINE = "line";
	
	private final TrackerNetData trackerNetData = new TrackerNetData();

	/**
	 * @see #EXTRA_LINE
	 */
	protected Line m_line;

	protected Calendar m_lastUpdated;

	protected final Set<String> m_expandedStationNames = new LinkedHashSet<>();

	protected SwipeRefreshLayout m_refresh;
	protected TextView m_status;

	protected ExpandableListView m_listView;
	protected PredictionSummaryAdapter m_adapter;

	protected ListViewHandler m_listHandler;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prediction_summary);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		restoreInstanceState(savedInstanceState);

		onCreate_setupCompassButtons();

		m_status = (TextView)findViewById(R.id.text_status);
		m_refresh = (SwipeRefreshLayout)findViewById(R.id.layout__wrapper);
		SwipeRefreshLayout.OnRefreshListener refresher = new SwipeRefreshLayout.OnRefreshListener() {
			@Override public void onRefresh() {
				startLoadingData();
			}
		};
		m_refresh.setOnRefreshListener(refresher);

		m_listView = (ExpandableListView)findViewById(android.R.id.list);
		m_listHandler = new ListViewHandler(this, m_listView, android.R.id.empty, refresher);
		m_listView.setOnGroupExpandListener(this);
		m_listView.setOnGroupCollapseListener(this);

		// gather params
		Intent intent = getIntent();
		m_line = IntentTools.getSerializableExtra(intent, EXTRA_LINE, Line.class);
		getSupportActionBar().setSubtitle(trackerNetData.getDisplayName(m_line));
	}

	protected void onCreate_setupCompassButtons() {
		buttons.put(PlatformDirection.East, (ToggleButton)this.findViewById(R.id.button__compass_east));
		buttons.put(PlatformDirection.West, (ToggleButton)this.findViewById(R.id.button__compass_west));
		buttons.put(PlatformDirection.North, (ToggleButton)this.findViewById(R.id.button__compass_north));
		buttons.put(PlatformDirection.South, (ToggleButton)this.findViewById(R.id.button__compass_south));
		buttons.put(PlatformDirection.Other, (ToggleButton)this.findViewById(R.id.button__compass_center));
		for (final Entry<PlatformDirection, ToggleButton> buttonMap : buttons.entrySet()) {
			// restore UI state (setChecked) before attaching the handler to prevent calling it
			buttonMap.getValue().setChecked(m_directionsEnabled.contains(buttonMap.getKey()));
			buttonMap.getValue().setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					toggleCompass(buttonMap.getKey(), isChecked);
				}
			});
		}
	}

	@Override protected void onResume() {
		super.onResume();
		startLoadingData();
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putLong("lastUpdate", m_lastUpdated.getTimeInMillis());
		outState.putStringArray("expanded", m_expandedStationNames.toArray(new String[m_expandedStationNames.size()]));
		outState.putSerializable("dirs",
				m_directionsEnabled.toArray(new PlatformDirection[m_directionsEnabled.size()]));
	}

	protected void restoreInstanceState(Bundle state) {
		if (state == null) {
			return;
		}
		m_lastUpdated = Calendar.getInstance();
		long lastUpdate = state.getLong("lastUpdate");
		if (lastUpdate != 0) {
			m_lastUpdated.setTimeInMillis(lastUpdate);
		}

		m_expandedStationNames.clear();
		String[] expandedNames = state.getStringArray("expanded");
		if (expandedNames != null) {
			m_expandedStationNames.addAll(Arrays.asList(expandedNames));
		}

		m_directionsEnabled.clear();
		PlatformDirection[] dirs = BundleTools.getSerializable(state, "dirs", PlatformDirection[].class);
		if (dirs != null) {
			m_directionsEnabled.addAll(Arrays.asList(dirs));
		}
	}

	private void startLoadingData() {
		m_refresh.setRefreshing(true);
		m_listHandler.startTFLLoad();
		delayedGetRoot();
	}

	private void delayedGetRoot() {
		Map<String, Object> args = new HashMap<>();
		args.put("line", m_line);
		@SuppressLint("StaticFieldLeak") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		@SuppressWarnings({"unused", "deprecation"}) // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		Object task = new DownloadFeedTask<PredictionSummaryFeed>(args) {
			@Override protected void onPostExecute(AsyncTaskResult<Feed, PredictionSummaryFeed> result) {
				if (result.getError() != null) {
					LOG.warn("Cannot load line prediction summary", result.getError());
					m_listHandler.empty("Cannot load line prediction summary: " + result.getError());
				} else if (result.getResult() == null) {
					LOG.warn("No line prediction summary returned", result.getError());
					m_listHandler.empty("No line prediction summary returned");
				} else {
					PredictionSummaryFeed root = result.getResult();
					root.setLine(m_line);
					m_lastUpdated = Calendar.getInstance();
					m_adapter = new PredictionSummaryAdapter(PredictionSummaryActivity.this, m_listView, map(root),
							m_directionsEnabled);
					m_listHandler.update("You've ruled out all stations, please loosen the filter.", m_adapter);
					m_status.setText(getString(R.string.last_updated, m_lastUpdated));
					restoreExpandedState();
				}
				m_refresh.setRefreshing(false);
			}
			private Map<Station, Map<Platform, List<Train>>> map(PredictionSummaryFeed root) {
				Map<Station, Map<Platform, List<Train>>> data = new TreeMap<>(Station.COMPARATOR_NAME);
				for (Station station : root.getStationPlatform().keySet()) {
					data.put(station, root.collectTrains(station));
				}
				return data;
			}
		}.execute(Feed.TubeDepartureBoardsPredictionSummary);
	}

	public void onGroupExpand(int groupPosition) {
		m_expandedStationNames.add(m_adapter.getGroup(groupPosition).getName());
	}
	public void onGroupCollapse(int groupPosition) {
		m_expandedStationNames.remove(m_adapter.getGroup(groupPosition).getName());
	}

	protected void restoreExpandedState() {
		if (m_adapter == null || m_expandedStationNames.isEmpty()) {
			return;
		}
		String lastExpanded = CollectionTools.last(m_expandedStationNames, true);
		int lastExpandedGroupIndex = -1;
		int groupIndex = 0;
		for (Station station : m_adapter.getGroups()) {
			if (m_expandedStationNames.contains(station.getName())) {
				m_listView.expandGroup(groupIndex);
			}
			if (station.getName().equals(lastExpanded)) {
				lastExpandedGroupIndex = groupIndex;
			}
			++groupIndex;
		}
		if (lastExpandedGroupIndex != -1) {
			m_listView.setSelectedGroup(lastExpandedGroupIndex);
			// FIXME doesn't work, maybe post async
			View selectedView = m_listView.getAdapter().getView(lastExpandedGroupIndex, null, m_listView);
			selectedView.measure(
					MeasureSpec.makeMeasureSpec(m_listView.getWidth(), MeasureSpec.AT_MOST),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
			);
			int height = selectedView.getMeasuredHeight();
			m_listView.scrollBy(0, -height / 2);
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.prediction_summary, menu);
		menuIDs.put(R.id.menu__option__compass_east, PlatformDirection.East);
		menuIDs.put(R.id.menu__option__compass_west, PlatformDirection.West);
		menuIDs.put(R.id.menu__option__compass_north, PlatformDirection.North);
		menuIDs.put(R.id.menu__option__compass_south, PlatformDirection.South);
		menuIDs.put(R.id.menu__option__compass_others, PlatformDirection.Other);
		menus.put(PlatformDirection.East, menu.findItem(R.id.menu__option__compass_east));
		menus.put(PlatformDirection.West, menu.findItem(R.id.menu__option__compass_west));
		menus.put(PlatformDirection.North, menu.findItem(R.id.menu__option__compass_north));
		menus.put(PlatformDirection.South, menu.findItem(R.id.menu__option__compass_south));
		menus.put(PlatformDirection.Other, menu.findItem(R.id.menu__option__compass_others));
		// fix checkboxes
		for (Entry<PlatformDirection, MenuItem> entry : menus.entrySet()) {
			entry.getValue().setChecked(m_directionsEnabled.contains(entry.getKey()));
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu__option__compass_east
				|| id == R.id.menu__option__compass_west
				|| id == R.id.menu__option__compass_north
				|| id == R.id.menu__option__compass_south
				|| id == R.id.menu__option__compass_others) {
			toggleCompass(menuIDs.get(item.getItemId()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected final Set<PlatformDirection> m_directionsEnabled = EnumSet.noneOf(PlatformDirection.class);
	protected final Map<Integer, PlatformDirection> menuIDs = new TreeMap<>();
	protected final Map<PlatformDirection, MenuItem> menus = new EnumMap<>(PlatformDirection.class);
	protected final Map<PlatformDirection, ToggleButton> buttons = new EnumMap<>(PlatformDirection.class);

	protected void resetCompassState() {
		PlatformDirection[] values = PlatformDirection.values();
		for (PlatformDirection dir : values) {
			toggleCompass(dir, m_directionsEnabled.contains(dir));
		}
	}
	protected void toggleCompass(PlatformDirection dir) {
		toggleCompass(dir, !m_directionsEnabled.contains(dir));
	}

	/**
	 * @return changed?
	 */
	protected boolean toggleCompass(PlatformDirection dir, boolean state) {
		if (state) {
			if (!m_directionsEnabled.add(dir)) {
				return false;
			}
		} else {
			if (!m_directionsEnabled.remove(dir)) {
				return false;
			}
		}

		MenuItem item = menus.get(dir);
		if (item != null) {
			item.setChecked(state);
		}

		ToggleButton button = buttons.get(dir);
		if (button != null) {
			button.setChecked(state);
		}

		if (m_adapter != null) { // data already loaded
			if (state) {
				m_adapter.addDirection(dir);
			} else {
				m_adapter.removeDirection(dir);
			}
			refreshAdapter();
		}
		return true;
	}

	protected void refreshAdapter() {
		m_adapter.notifyDataSetChanged();
		m_listView.setAdapter(m_adapter);
		restoreExpandedState();
	}
}
