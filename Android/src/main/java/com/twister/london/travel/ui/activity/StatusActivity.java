package com.twister.london.travel.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.*;

import com.handmark.pulltorefresh.library.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.twister.android.utils.concurrent.AsyncTaskResult;
import com.twister.london.travel.R;
import com.twister.london.travel.io.feeds.*;
import com.twister.london.travel.model.LineStatus;
import com.twister.london.travel.ui.adapter.StationStatusAdapter;

/**
 * http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf
 * @author TWiStEr
 */
public class StatusActivity extends ListActivity implements OnRefreshListener<ListView> {
	private PullToRefreshListView m_listView;

	private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar m_lastUpdated;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_status);
		m_listView = (PullToRefreshListView)findViewById(R.id.asdf_list);
		m_listView.setOnRefreshListener(this);

		this.onRefresh(m_listView);
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		delayedGetRoot();
	}

	private void delayedGetRoot() {
		new DownloadFeedTask<LineStatusFeed>() {
			protected void onPostExecute(AsyncTaskResult<LineStatusFeed> result) {
				if (result.getError() != null) {
					LOG.error("Cannot load line statuses", result.getError());
					Toast.makeText(getApplicationContext(), "Cannot load line statuses" + result.getError(),
							Toast.LENGTH_LONG);
				} else if (result.getResult() == null) {
					LOG.error("No line statuses returned", result.getError());
					Toast.makeText(getApplicationContext(), "No line statuses returned", Toast.LENGTH_LONG);
				} else {
					LineStatusFeed root = result.getResult();
					dataReceived(root);
				}
			}
		}.execute(Feed.TubeDepartureBoardsLineStatus);
	}

	protected void dataReceived(LineStatusFeed root) {
		List<LineStatus> lines = root.getLineStatuses();
		setListAdapter(new StationStatusAdapter(StatusActivity.this, lines));
		m_lastUpdated = Calendar.getInstance();
		m_listView.getLoadingLayoutProxy()
				.setLastUpdatedLabel("Last updated at " + fmt.format(m_lastUpdated.getTime()));
		m_listView.onRefreshComplete();
	};
}
