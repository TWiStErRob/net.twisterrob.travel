package net.twisterrob.blt.android.ui.activity;

import java.text.SimpleDateFormat;
import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.adapter.*;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter.Direction;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.Line;
import android.annotation.SuppressLint;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.*;
import android.widget.*;

import com.handmark.pulltorefresh.library.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

/**
 * http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf
 * @author TWiStEr
 */
public class PredictionSummaryActivity extends ExpandableListActivity implements OnRefreshListener<ExpandableListView> {
	public static final String EXTRA_LINE = "line";

	/**
	 * @see #EXTRA_LINE
	 */
	private Line m_line;

	private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Calendar m_lastUpdated;

	private final Set<Integer> expandedGroupPositions = new TreeSet<Integer>();

	private PullToRefreshExpandableListView m_refreshView;

	private PredictionSummaryAdapter m_adapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prediction_summary);

		m_refreshView = (PullToRefreshExpandableListView)findViewById(R.id.asdf_list);
		m_refreshView.setOnRefreshListener(this);

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
			setListAdapter(m_adapter);
			m_lastUpdated = Calendar.getInstance();
			m_refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					"Last updated at " + fmt.format(m_lastUpdated.getTime()));
		}
		m_refreshView.onRefreshComplete();
		for (Integer groupPosition: expandedGroupPositions) {
			m_refreshView.getRefreshableView().expandGroup(groupPosition);
		}
	}

	@Override
	public void onGroupExpand(int groupPosition) {
		expandedGroupPositions.add(groupPosition);
	}
	@Override
	public void onGroupCollapse(int groupPosition) {
		expandedGroupPositions.remove(groupPosition);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_prediction_summary, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Direction dir = null;
		switch (item.getItemId()) {
			case R.id.menu_context_prediction_summary_direction_east:
				item.setChecked(!item.isChecked());
				dir = Direction.East;
				break;
			case R.id.menu_context_prediction_summary_direction_west:
				item.setChecked(!item.isChecked());
				dir = Direction.West;
				break;
			case R.id.menu_context_prediction_summary_direction_north:
				item.setChecked(!item.isChecked());
				dir = Direction.North;
				break;
			case R.id.menu_context_prediction_summary_direction_south:
				item.setChecked(!item.isChecked());
				dir = Direction.South;
				break;
			case R.id.menu_context_prediction_summary_direction_others:
				item.setChecked(!item.isChecked());
				dir = Direction.Other;
				break;
			default:
				return super.onMenuItemSelected(featureId, item);
		}
		if (dir != null && m_adapter != null) {
			if (item.isChecked()) {
				m_adapter.addDirection(dir);
			} else {
				m_adapter.removeDirection(dir);
			}
			m_adapter.notifyDataSetChanged();
		}
		return true;
	}
}
