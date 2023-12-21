package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.widget.*;
import android.widget.ExpandableListView.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.app.full.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.ListViewHandler;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.trackernet.PredictionSummaryFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.*;
import net.twisterrob.blt.model.*;

public class StationInfoActivity extends BaseActivity implements
		OnGroupExpandListener,
		OnGroupCollapseListener {
	public static final String EXTRA_STATION_NAME = "name";

	/**
	 * @see #EXTRA_STATION_NAME
	 */
	protected net.twisterrob.blt.android.db.model.Station m_station;

	protected Calendar m_lastUpdated;

	protected SwipeRefreshLayout m_refresh;
	protected TextView m_status;

	protected ExpandableListView m_listView;
	protected PredictionSummaryAdapter m_adapter;

	protected ListViewHandler m_listHandler;

	private final Map<Station, Map<Platform, List<Train>>> m_map = new TreeMap<>(
			new Comparator<Station>() {
				public int compare(Station lhs, Station rhs) {
					int first = Station.COMPARATOR_NAME.compare(lhs, rhs);
					return first != 0? first : lhs.getLine().compareTo(rhs.getLine());
				}
			});

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_info);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		m_status = (TextView)findViewById(R.id.text_status);
		m_refresh = (SwipeRefreshLayout)findViewById(R.id.layout__wrapper);
		SwipeRefreshLayout.OnRefreshListener refresher = new SwipeRefreshLayout.OnRefreshListener() {
			@Override public void onRefresh() {
				startLoadingData();
			}
		};
		m_refresh.setOnRefreshListener(refresher);

		m_listView = (ExpandableListView)findViewById(android.R.id.list);
		m_adapter = new PredictionSummaryAdapter(this, m_listView, m_map, Collections.<PlatformDirection>emptySet());
		m_listHandler = new ListViewHandler(this, m_listView, android.R.id.empty, refresher);
		m_listHandler.update("You've ruled out all stations, please loosen the filter.", m_adapter);

		m_listView.setOnGroupExpandListener(this);
		m_listView.setOnGroupCollapseListener(this);

		// gather params
		Intent intent = getIntent();
		String name = intent.getStringExtra(EXTRA_STATION_NAME);
		m_station = App.db().getStation(name);

		getSupportActionBar().setSubtitle(name);
		((TextView)findViewById(R.id.text_station)).setText(name);
	}

	@Override protected void onResume() {
		super.onResume();
		startLoadingData();
	}

	private void startLoadingData() {
		m_refresh.setRefreshing(true);
		m_listHandler.startTFLLoad();
		delayedGetRoot();
	}

	private void delayedGetRoot() {
		m_doneLines.clear();
		for (final Line line : m_station.getLines()) {
			Map<String, Object> args = new HashMap<>();
			args.put("line", line);
			args.put("station", m_station.getTrackerNetCode(line));
			@SuppressLint("StaticFieldLeak") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
			@SuppressWarnings({"unused", "deprecation"}) // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
			Object task = new DownloadFeedTask<PredictionSummaryFeed>(args) {
				@Override protected void onPostExecute(AsyncTaskResult<Feed, PredictionSummaryFeed> result) {
					if (result.getError() != null) {
						LOG.warn("Cannot load line prediction summary", result.getError());
						m_listHandler.empty("Cannot load line prediction summary: " + result.getError());
					} else if (result.getResult() == null) {
						LOG.warn("No line prediction summary returned", result.getError());
						// TODO m_listHandler.empty("No line prediction summary returned");
						addResult(null, line);
					} else {
						PredictionSummaryFeed root = result.getResult();
						addResult(root, root.getLine());
					}
					m_refresh.setRefreshing(false);
				}
			}.execute(Feed.TubeDepartureBoardsPredictionDetailed);
		}
	}

	List<Line> m_doneLines = new ArrayList<>();

	protected synchronized void addResult(PredictionSummaryFeed root, Line line) {
		m_lastUpdated = Calendar.getInstance();
		m_status.setText(getString(R.string.last_updated, m_lastUpdated));
		m_doneLines.add(line);
		if (root != null) {
			Map<Station, Map<Platform, List<Train>>> map = map(root);
			m_map.putAll(map);
		}
		if (m_station.getLines().size() == m_doneLines.size()) {
			m_adapter.notifyDataSetChanged();
			m_listHandler.update("Done " + m_station.getLines() + " vs " + m_doneLines, m_adapter);
		} else {
			m_listHandler.empty("Pending " + m_station.getLines() + " vs " + m_doneLines);
		}
	}

	private static Map<Station, Map<Platform, List<Train>>> map(PredictionSummaryFeed root) {
		Map<Station, Map<Platform, List<Train>>> data = new TreeMap<>(Station.COMPARATOR_NAME);
		for (net.twisterrob.blt.io.feeds.trackernet.model.Station station : root.getStationPlatform().keySet()) {
			data.put(station, root.collectTrains(station));
		}
		return data;
	}

	public void onGroupExpand(int groupPosition) {
		//m_expandedStationNames.add(m_adapter.getGroup(groupPosition).getName());
	}
	public void onGroupCollapse(int groupPosition) {
		//m_expandedStationNames.remove(m_adapter.getGroup(groupPosition).getName());
	}
}
