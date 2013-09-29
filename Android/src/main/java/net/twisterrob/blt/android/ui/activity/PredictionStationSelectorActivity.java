package net.twisterrob.blt.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.adapter.LinePredictionSummaryAdapter;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.Line;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.os.*;
import android.support.v4.app.NavUtils;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

import com.handmark.pulltorefresh.library.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

/**
 * http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf
 * @author TWiStEr
 */
public class PredictionStationSelectorActivity extends ListActivity implements OnRefreshListener<ListView> {
	public static final String EXTRA_LINE = "line";

	private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar m_lastUpdated;

	private PullToRefreshListView m_refreshView;
	private ListView m_listView;

	/**
	 * @see #EXTRA_LINE
	 */
	private Line m_line;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);

		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			// TODO getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		m_refreshView = (PullToRefreshListView)findViewById(R.id.asdf_list);
		m_refreshView.setOnRefreshListener(this);

		m_listView = m_refreshView.getRefreshableView();
		m_listView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// StationPrediction = (StationPrediction)parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});

		// gather params
		Intent intent = getIntent();
		m_line = (Line)intent.getSerializableExtra(EXTRA_LINE);

		// actually start loading the data
		this.onRefresh(m_refreshView);
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
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
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
			setListAdapter(new LinePredictionSummaryAdapter(PredictionStationSelectorActivity.this, root));
			m_lastUpdated = Calendar.getInstance();
			m_refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"Last updated at " + fmt.format(m_lastUpdated.getTime()));
		}
		m_refreshView.onRefreshComplete();
	}
}
