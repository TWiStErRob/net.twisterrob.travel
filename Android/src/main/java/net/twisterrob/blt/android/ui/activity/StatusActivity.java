package net.twisterrob.blt.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.adapter.StationStatusAdapter;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.*;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
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

	private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar m_lastUpdated;

	private PullToRefreshAttacher m_pullToRefreshAttacher;
	private ListView m_listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		m_pullToRefreshAttacher = PullToRefreshAttacher.get(this);

		m_listView = (ListView)findViewById(android.R.id.list);
		m_pullToRefreshAttacher.addRefreshableView(m_listView, this);

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
		delayedGetRoot();
	}

	private void delayedGetRoot() {
		new DownloadFeedTask<LineStatusFeed>() {
			protected void onPostExecute(AsyncTaskResult<LineStatusFeed> result) {
				if (result.getError() != null) {
					LOG.error("Cannot load line statuses", result.getError());
					Toast.makeText(getApplicationContext(), "Cannot load line statuses: " + result.getError(),
							Toast.LENGTH_LONG).show();
					dataReceived(null);
				} else if (result.getResult() == null) {
					LOG.error("No line statuses returned", result.getError());
					Toast.makeText(getApplicationContext(), "No line statuses returned", Toast.LENGTH_LONG).show();
					dataReceived(null);
				} else {
					LineStatusFeed root = result.getResult();
					dataReceived(root);
				}
			}
		}.execute(Feed.TubeDepartureBoardsLineStatus);
	}

	protected void dataReceived(LineStatusFeed root) {
		if (root != null) {
			List<LineStatus> lines = root.getLineStatuses();
			m_listView.setAdapter(new StationStatusAdapter(StatusActivity.this, lines));
			m_lastUpdated = Calendar.getInstance();
			String lastUpdateText = "Last updated at " + fmt.format(m_lastUpdated.getTime());
			// TODO m_refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(lastUpdateText);
		}
		m_pullToRefreshAttacher.setRefreshComplete();
	}
}
