package net.twisterrob.blt.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.*;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.trackernet.PredictionSummaryFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.*;
import net.twisterrob.blt.model.*;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.*;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class StationInfoActivity extends ActionBarActivity
		implements
			OnRefreshListener,
			OnGroupExpandListener,
			OnGroupCollapseListener {
	public static final String EXTRA_STATION_NAME = "name";

	/**
	 * @see #EXTRA_STATION_NAME
	 */
	protected net.twisterrob.blt.android.db.model.Station m_station;

	protected final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected Calendar m_lastUpdated;

	protected AppCompatPullToRefreshAttacher m_ptrAttacher;

	protected ExpandableListView m_listView;
	protected PredictionSummaryAdapter m_adapter;

	protected ListViewHandler m_listHandler;

	private final Map<Station, Map<Platform, List<Train>>> m_map = new TreeMap<Station, Map<Platform, List<Train>>>(
			new Comparator<Station>() {
				public int compare(Station lhs, Station rhs) {
					int first = Station.COMPARATOR_NAME.compare(lhs, rhs);
					return first != 0? first : lhs.getLine().compareTo(rhs.getLine());
				}
			});

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_info);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		m_listView = (ExpandableListView)findViewById(android.R.id.list);
		m_adapter = new PredictionSummaryAdapter(StationInfoActivity.this, m_listView, m_map,
				Collections.<PlatformDirection> emptySet());
		m_listHandler = new ListViewHandler(this, m_listView, android.R.id.empty);
		m_listHandler.update("You've ruled out all stations, please loosen the filter.", m_adapter);

		m_ptrAttacher = AppCompatPullToRefreshAttacher.get(this).init(R.id.layout$wrapper, this);

		m_listView.setOnGroupExpandListener(this);
		m_listView.setOnGroupCollapseListener(this);

		// gather params
		Intent intent = getIntent();
		String name = (String)intent.getSerializableExtra(EXTRA_STATION_NAME);
		m_station = App.getInstance().getDataBaseHelper().getStation(name);

		getSupportActionBar().setSubtitle(name);
		((TextView)findViewById(R.id.text_station)).setText(name);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// actually start loading the data
		this.onRefreshStarted(m_listView);
	}

	@Override
	public void onRefreshStarted(View view) {
		m_listHandler.startTFLLoad();
		m_ptrAttacher.setRefreshing(true);
		delayedGetRoot();
	}

	private void delayedGetRoot() {
		m_doneLines.clear();
		for (final Line line: m_station.getLines()) {
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("line", line);
			args.put("station", m_station.getTrackerNetCode(line));
			new DownloadFeedTask<PredictionSummaryFeed>(args) {
				@Override
				protected void onPostExecute(AsyncTaskResult<PredictionSummaryFeed> result) {
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
					m_ptrAttacher.setRefreshComplete();
				}
			}.execute(Feed.TubeDepartureBoardsPredictionDetailed);
		}
	}

	List<Line> m_doneLines = new ArrayList<Line>();

	protected synchronized void addResult(PredictionSummaryFeed root, Line line) {
		m_lastUpdated = Calendar.getInstance();
		m_ptrAttacher.setLastUpdated("Last updated at " + fmt.format(m_lastUpdated.getTime()));
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
		Map<Station, Map<Platform, List<Train>>> data = new TreeMap<Station, Map<Platform, List<Train>>>(
				Station.COMPARATOR_NAME);
		for (net.twisterrob.blt.io.feeds.trackernet.model.Station station: root.getStationPlatform().keySet()) {
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
