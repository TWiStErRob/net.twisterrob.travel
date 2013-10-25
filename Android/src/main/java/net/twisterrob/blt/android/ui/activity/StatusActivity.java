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
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

/**
 * http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf
 * @author TWiStEr
 */
public class StatusActivity extends ActionBarActivity implements OnRefreshListener {

	protected SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected Calendar m_lastUpdated;

	protected AppCompatPullToRefreshAttacher m_ptrAttacher;
	protected ListView m_listView;
	protected ListViewHandler m_listHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		m_ptrAttacher = AppCompatPullToRefreshAttacher.get(this).init(R.id.layout$wrapper, this);

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
		this.onRefreshStarted(m_listView);
	}

	@Override
	public void onRefreshStarted(View view) {
		m_listHandler.startTFLLoad();
		delayedGetRoot();
	}

	private void delayedGetRoot() {
		new DownloadFeedTask<LineStatusFeed>() {
			@Override
			protected void onPostExecute(AsyncTaskResult<LineStatusFeed> result) {
				if (result.getError() != null) {
					String msg = "Cannot load line statuses: " + result.getError();
					LOG.error(msg, result.getError());
					m_listHandler.empty(msg);
				} else if (result.getResult() == null) {
					String msg = "No line statuses returned";
					LOG.error(msg, result.getError());
					m_listHandler.empty(msg);
				} else {
					LineStatusFeed root = result.getResult();
					List<LineStatus> lines = root.getLineStatuses();
					ListAdapter adapter = new StationStatusAdapter(StatusActivity.this, lines);
					m_listHandler.update("No data present", adapter);
					m_lastUpdated = Calendar.getInstance();
					m_ptrAttacher.setLastUpdated("Last updated at " + fmt.format(m_lastUpdated.getTime()));
				}
				m_ptrAttacher.setRefreshComplete();
			}
		}.execute(Feed.TubeDepartureBoardsLineStatus);
	}
}
