package net.twisterrob.blt.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.adapter.*;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter.Direction;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.*;
import android.annotation.SuppressLint;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
public class PredictionSummaryActivity extends ExpandableListActivity
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

	private final Set<String> m_expandedGroupPositions = new TreeSet<String>();

	private PullToRefreshExpandableListView m_refreshView;

	private PredictionSummaryAdapter m_adapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prediction_summary);
		buttons.put(Direction.East, (ToggleButton)this.findViewById(R.id.button_compass_east));
		buttons.put(Direction.West, (ToggleButton)this.findViewById(R.id.button_compass_west));
		buttons.put(Direction.North, (ToggleButton)this.findViewById(R.id.button_compass_north));
		buttons.put(Direction.South, (ToggleButton)this.findViewById(R.id.button_compass_south));
		buttons.put(Direction.Other, (ToggleButton)this.findViewById(R.id.button_compass_center));
		for (final Entry<Direction, ToggleButton> buttonMap: buttons.entrySet()) {
			buttonMap.getValue().setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					toggleCompass(buttonMap.getKey(), isChecked);
				}
			});
		}

		m_refreshView = (PullToRefreshExpandableListView)findViewById(R.id.wrapper);
		m_refreshView.setOnRefreshListener(this);

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
		Log.d("expandedGroupPositions", "" + m_expandedGroupPositions.hashCode());
		outState.putLong("lastUpdate", m_lastUpdated.getTimeInMillis());
		outState.putStringArray("expanded",
				m_expandedGroupPositions.toArray(new String[m_expandedGroupPositions.size()]));
		outState.putSerializable("dirs", directionsEnabled.toArray(new Direction[directionsEnabled.size()]));
	}
	@Override
	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		Log.d("expandedGroupPositions", "" + m_expandedGroupPositions.hashCode());

		m_lastUpdated = Calendar.getInstance();
		long lastUpdate = state.getLong("lastUpdate");
		if (lastUpdate != 0) {
			m_lastUpdated.setTimeInMillis(lastUpdate);
		}

		m_expandedGroupPositions.clear();
		String[] expandedNames = state.getStringArray("expanded");
		if (expandedNames != null) {
			m_expandedGroupPositions.addAll(Arrays.asList(expandedNames));
		}

		directionsEnabled.clear();
		Direction[] dirs = (Direction[])state.getSerializable("dirs");
		if (dirs != null) {
			directionsEnabled.addAll(Arrays.asList(dirs));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView) {
		m_refreshView.setRefreshing();
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
			setListAdapter(m_adapter);
			m_lastUpdated = Calendar.getInstance();
			m_refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"Last updated at " + fmt.format(m_lastUpdated.getTime()));
		}
		m_refreshView.onRefreshComplete();
		restoreExpandedState();
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		m_expandedGroupPositions.add(m_adapter.getGroup(groupPosition).getName());
	}
	@Override
	public void onGroupCollapse(int groupPosition) {
		m_expandedGroupPositions.remove(m_adapter.getGroup(groupPosition).getName());
	}
	protected void restoreExpandedState() {
		for (String stationName: m_expandedGroupPositions) {
			int groupIndex = 0;
			for (Station station: m_adapter.getFilteredGroups()) {
				if (station.getName().equals(stationName)) {
					m_refreshView.getRefreshableView().expandGroup(groupIndex);
					break;
				}
				++groupIndex;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_prediction_summary, menu);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_east, Direction.East);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_west, Direction.West);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_north, Direction.North);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_south, Direction.South);
		menuIDs.put(R.id.menu_context_prediction_summary_direction_others, Direction.Other);
		menus.put(Direction.East, menu.findItem(R.id.menu_context_prediction_summary_direction_east));
		menus.put(Direction.West, menu.findItem(R.id.menu_context_prediction_summary_direction_west));
		menus.put(Direction.North, menu.findItem(R.id.menu_context_prediction_summary_direction_north));
		menus.put(Direction.South, menu.findItem(R.id.menu_context_prediction_summary_direction_south));
		menus.put(Direction.Other, menu.findItem(R.id.menu_context_prediction_summary_direction_others));
		resetCompassState();
		return true;
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_context_prediction_summary_direction_east:
			case R.id.menu_context_prediction_summary_direction_west:
			case R.id.menu_context_prediction_summary_direction_north:
			case R.id.menu_context_prediction_summary_direction_south:
			case R.id.menu_context_prediction_summary_direction_others:
				toggleCompass(menuIDs.get(item.getItemId()));
				break;
			default:
				return super.onMenuItemSelected(featureId, item);
		}
		return true;
	}

	private final Set<Direction> directionsEnabled = EnumSet.allOf(Direction.class);
	private final Map<Integer, Direction> menuIDs = new TreeMap<Integer, Direction>();
	private final Map<Direction, MenuItem> menus = new EnumMap<Direction, MenuItem>(Direction.class);
	private final Map<Direction, ToggleButton> buttons = new EnumMap<Direction, ToggleButton>(Direction.class);

	protected void resetCompassState() {
		Log.d("compass", new Throwable().getStackTrace()[1].toString());
		for (Direction dir: Direction.values()) {
			toggleCompass(dir, directionsEnabled.contains(dir));
		}
	}
	protected void toggleCompass(Direction dir) {
		toggleCompass(dir, !directionsEnabled.contains(dir));
	}
	protected void toggleCompass(Direction dir, boolean state) {
		if (state) {
			directionsEnabled.add(dir);
		} else {
			directionsEnabled.remove(dir);
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
			setListAdapter(m_adapter);
			m_adapter.notifyDataSetChanged();
			restoreExpandedState();
		}
	}
}
