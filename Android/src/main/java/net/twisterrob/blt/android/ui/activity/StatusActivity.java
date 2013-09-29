package net.twisterrob.blt.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.adapter.StationStatusAdapter;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.*;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

/**
 * http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf
 * @author TWiStEr
 */
public class StatusActivity extends ListActivity implements OnRefreshListener<ListView> {

	private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar m_lastUpdated;

	private PullToRefreshListView m_refreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		m_refreshView = (PullToRefreshListView)findViewById(R.id.asdf_list);
		m_refreshView.setOnRefreshListener(this);

		m_refreshView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LineStatus status = (LineStatus)parent.getItemAtPosition(position);
				Intent intent = new Intent(StatusActivity.this, PredictionStationSelectorActivity.class);
				{
					Line line = status.getLine();
					intent.putExtra(PredictionStationSelectorActivity.EXTRA_LINE, line);
				}
				startActivity(intent);
			}
		});

		// actually start loading the data
		this.onRefresh(m_refreshView);
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
			setListAdapter(new StationStatusAdapter(StatusActivity.this, lines));
			m_lastUpdated = Calendar.getInstance();
			m_refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"Last updated at " + fmt.format(m_lastUpdated.getTime()));
		}
		m_refreshView.onRefreshComplete();
	}
}
