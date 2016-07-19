package net.twisterrob.blt.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.*;
import net.twisterrob.blt.android.ui.adapter.StationStatusAdapter;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus;
import net.twisterrob.blt.model.Line;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

/**
 * http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf
 * @author TWiStEr
 */
public class StatusActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

	protected SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected Calendar m_lastUpdated;

	protected SwipeRefreshLayout m_refresh;
	protected TextView m_status;
	
	protected ListView m_listView;
	protected ListViewHandler m_listHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		m_status = (TextView)findViewById(R.id.text_status);
		m_refresh = (SwipeRefreshLayout)findViewById(R.id.layout$wrapper);
		m_refresh.setOnRefreshListener(this);

		m_listView = (ListView)findViewById(android.R.id.list);
		m_listHandler = new ListViewHandler(this, m_listView, android.R.id.empty);
		m_listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LineStatus status = (LineStatus)parent.getItemAtPosition(position);
				Intent intent = new Intent(StatusActivity.this, PredictionSummaryActivity.class);
				{
					Line line = status.getLine();
					intent.putExtra(PredictionSummaryActivity.EXTRA_LINE, line);
				}
				startActivity(intent);
			}
		});

		// actually start loading the data
		this.onRefresh();
	}

	@Override public void onRefresh() {
		m_refresh.setRefreshing(true);
		m_listHandler.startTFLLoad();
		delayedGetRoot();
	}

	private void delayedGetRoot() {
		new DownloadFeedTask<LineStatusFeed>() {
			@Override
			protected void onPostExecute(AsyncTaskResult<Feed, LineStatusFeed> result) {
				if (result.getError() != null) {
					LOG.warn("Cannot load line statuses", result.getError());
					m_listHandler.empty("Cannot load line statuses: " + result.getError());
				} else if (result.getResult() == null) {
					LOG.warn("No line statuses returned", result.getError());
					m_listHandler.empty("No line statuses returned");
				} else {
					LineStatusFeed root = result.getResult();
					List<LineStatus> lines = root.getLineStatuses();
					ListAdapter adapter = new StationStatusAdapter(StatusActivity.this, lines);
					m_listHandler.update("No data present", adapter);
					m_lastUpdated = Calendar.getInstance();
					m_status.setText("Last updated at " + fmt.format(m_lastUpdated.getTime()));
				}
				m_refresh.setRefreshing(false);
			}
		}.execute(Feed.TubeDepartureBoardsLineStatus);
	}
}
