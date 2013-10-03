package net.twisterrob.blt.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import com.handmark.pulltorefresh.library.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

/**
 * http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf
 * @author TWiStEr
 */
public class PredictionSummaryActivity extends ActionBarActivity
		implements
			OnRefreshListener<ExpandableListView>,
			OnGroupExpandListener,
			OnGroupCollapseListener {
	public static final String EXTRA_LINE = "line";

	/**
	 * @see #EXTRA_LINE
	 */
	private Line m_line;

	private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar m_lastUpdated;

	private final Set<String> m_expandedStationNames = new TreeSet<String>();

	private PullToRefreshExpandableListView m_refreshView;

	private PredictionSummaryAdapter m_adapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prediction_summary);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		buttons.put(PlatformDirection.East, (ToggleButton)this.findViewById(R.id.button_compass_east));
		buttons.put(PlatformDirection.West, (ToggleButton)this.findViewById(R.id.button_compass_west));
		buttons.put(PlatformDirection.North, (ToggleButton)this.findViewById(R.id.button_compass_north));
		buttons.put(PlatformDirection.South, (ToggleButton)this.findViewById(R.id.button_compass_south));
		buttons.put(PlatformDirection.Other, (ToggleButton)this.findViewById(R.id.button_compass_center));
		for (final Entry<PlatformDirection, ToggleButton> buttonMap: buttons.entrySet()) {
			buttonMap.getValue().setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					toggleCompass(buttonMap.getKey(), isChecked);
				}
			});
		}

		m_refreshView = (PullToRefreshExpandableListView)findViewById(R.id.wrapper);
		m_refreshView.setOnRefreshListener(this);
		m_refreshView.getRefreshableView().setOnGroupExpandListener(this);
		m_refreshView.getRefreshableView().setOnGroupCollapseListener(this);

		// gather params
		Intent intent = getIntent();
		m_line = (Line)intent.getSerializableExtra(EXTRA_LINE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// actually start loading the data
		this.onRefresh(m_refreshView);
		resetCompassState();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("expandedGroupPositions", "" + m_expandedStationNames.hashCode());
		outState.putLong("lastUpdate", m_lastUpdated.getTimeInMillis());
		outState.putStringArray("expanded", m_expandedStationNames.toArray(new String[m_expandedStationNames.size()]));
		outState.putSerializable("dirs", m_directionsEnabled.toArray(new PlatformDirection[m_directionsEnabled.size()]));
	}
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		Log.d("expandedGroupPositions", "" + m_expandedStationNames.hashCode());

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
		PlatformDirection[] dirs = (PlatformDirection[])state.getSerializable("dirs");
		if (dirs != null) {
			m_directionsEnabled.addAll(Arrays.asList(dirs));
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
		refreshView.setRefreshing();
		delayedGetRoot();
	}

	private void delayedGetRoot() {
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("line", m_line);
		new DownloadFeedTask<PredictionSummaryFeed>(args) {
			protected void onPostExecute(AsyncTaskResult<PredictionSummaryFeed> result) {
				if (result.getError() != null) {
					LOG.error("Cannot load line prediction summary", result.getError());
					Toast.makeText(getApplicationContext(), "Cannot load line prediction summary" + result.getError(),
							Toast.LENGTH_LONG).show();
					dataReceived(null);
				} else if (result.getResult() == null) {
					LOG.error("No line prediction summary returned", result.getError());
					Toast.makeText(getApplicationContext(), "No line prediction summary returned", Toast.LENGTH_LONG)
							.show();
					dataReceived(null);
				} else {
					PredictionSummaryFeed root = result.getResult();
					root.setLine(m_line);
					dataReceived(root);
				}
			}
		}.execute(Feed.TubeDepartureBoardsPredictionSummary);
	}

	protected void dataReceived(PredictionSummaryFeed root) {
		if (root != null) {
			m_adapter = new PredictionSummaryAdapter(PredictionSummaryActivity.this, root);
			resetCompassState();
			m_lastUpdated = Calendar.getInstance();
			m_refreshView.getRefreshableView().setAdapter(m_adapter);
			m_refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"Last updated at " + fmt.format(m_lastUpdated.getTime()));
		}
		m_refreshView.onRefreshComplete();
		restoreExpandedState();
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		m_expandedStationNames.add(m_adapter.getGroup(groupPosition).getName());
	}
	@Override
	public void onGroupCollapse(int groupPosition) {
		m_expandedStationNames.remove(m_adapter.getGroup(groupPosition).getName());
	}

	protected void restoreExpandedState() {
		int groupIndex = 0;
		for (Station station: m_adapter.getGroups()) {
			if (m_expandedStationNames.contains(station.getName())) {
				m_refreshView.getRefreshableView().expandGroup(groupIndex);
			}
			++groupIndex;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_prediction_summary, menu);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_east, PlatformDirection.East);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_west, PlatformDirection.West);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_north, PlatformDirection.North);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_south, PlatformDirection.South);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_others, PlatformDirection.Other);
		menus.put(PlatformDirection.East, menu.findItem(R.id.menu_context_prediction_summary_direction_east));
		menus.put(PlatformDirection.West, menu.findItem(R.id.menu_context_prediction_summary_direction_west));
		menus.put(PlatformDirection.North, menu.findItem(R.id.menu_context_prediction_summary_direction_north));
		menus.put(PlatformDirection.South, menu.findItem(R.id.menu_context_prediction_summary_direction_south));
		menus.put(PlatformDirection.Other, menu.findItem(R.id.menu_context_prediction_summary_direction_others));
		// fix checkboxes
		for (Entry<PlatformDirection, MenuItem> entry: menus.entrySet()) {
			entry.getValue().setChecked(m_directionsEnabled.contains(entry.getKey()));
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_context_prediction_summary_direction_east:
			case R.id.menu_context_prediction_summary_direction_west:
			case R.id.menu_context_prediction_summary_direction_north:
			case R.id.menu_context_prediction_summary_direction_south:
			case R.id.menu_context_prediction_summary_direction_others:
				toggleCompass(menuIDs.get(item.getItemId()));
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private final Set<PlatformDirection> m_directionsEnabled = EnumSet.allOf(PlatformDirection.class);
	private final Map<Integer, PlatformDirection> menuIDs = new TreeMap<Integer, PlatformDirection>();
	private final Map<PlatformDirection, MenuItem> menus = new EnumMap<PlatformDirection, MenuItem>(
			PlatformDirection.class);
	private final Map<PlatformDirection, ToggleButton> buttons = new EnumMap<PlatformDirection, ToggleButton>(
			PlatformDirection.class);

	protected void resetCompassState() {
		Log.e("compass", new Throwable().getStackTrace()[1].toString());
		for (PlatformDirection dir: PlatformDirection.values()) {
			toggleCompass(dir, m_directionsEnabled.contains(dir));
		}
	}
	protected void toggleCompass(PlatformDirection dir) {
		toggleCompass(dir, !m_directionsEnabled.contains(dir));
	}
	protected void toggleCompass(PlatformDirection dir, boolean state) {
		if (state) {
			m_directionsEnabled.add(dir);
		} else {
			m_directionsEnabled.remove(dir);
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
			m_refreshView.getRefreshableView().setAdapter(m_adapter);
			m_adapter.notifyDataSetChanged();
			restoreExpandedState();
		}
	}
}
