package com.twister.london.travel.ui.activity;

import java.net.MalformedURLException;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.twister.android.utils.concurrent.AsyncTaskResult;
import com.twister.android.utils.log.*;
import com.twister.london.travel.io.feeds.*;
import com.twister.london.travel.io.feeds.android.DownloadFeedTask;
import com.twister.london.travel.model.*;
import com.twister.london.travel.ui.adapter.StationStatusAdapter;

public class StatusActivity extends ListActivity {
	private static final Log LOG = LogFactory.getLog(Tag.UI);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			delayedGetRoot();
		} catch (MalformedURLException ex) {
			LOG.error("Cannot create URL.", ex);
		}
	}

	private void delayedGetRoot() throws MalformedURLException {
		new DownloadFeedTask<LineStatusFeed>() {
			protected void onPostExecute(AsyncTaskResult<LineStatusFeed> result) {
				Map<Line, LineStatus> lines;
				if (result.getError() != null) {
					LOG.error("Cannot load line statuses", result.getError());
					Toast.makeText(getApplicationContext(), "Cannot load line statuses" + result.getError(),
							Toast.LENGTH_LONG);
				} else if (result.getResult() == null) {
					LOG.error("No line statuses returned", result.getError());
					Toast.makeText(getApplicationContext(), "No line statuses returned", Toast.LENGTH_LONG);
				} else {
					LineStatusFeed root = result.getResult();
					lines = root.getLineStatuses();
					setListAdapter(new StationStatusAdapter(StatusActivity.this, lines.entrySet()));
				}
			};
		}.execute(Feed.TubeDepartureBoardsLineStatus);
	}
}
