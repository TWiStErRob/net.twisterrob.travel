package com.twister.london.travel.ui.activity;

import java.io.*;
import java.net.*;
import java.util.List;

import android.app.ListActivity;
import android.os.*;

import com.twister.android.utils.concurrent.AsyncTaskResult;
import com.twister.android.utils.log.*;
import com.twister.london.travel.App;
import com.twister.london.travel.io.feeds.*;
import com.twister.london.travel.model.Station;
import com.twister.london.travel.ui.adapter.StationAdapter;

public class MainActivity extends ListActivity {
	private static final Log LOG = LogFactory.getLog(Tag.UI);
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		delayedGetRoot();
	}

	private void delayedGetRoot() {
		new DownloadFilesTask() {
			protected void onPostExecute(AsyncTaskResult<FacilitiesFeed> result) {
				List<Station> stations;
				if (result.getError() != null) {
					LOG.error("Cannot load facitilies", result.getError());
					stations = App.getInstance().getDataBaseHelper().getStations();
				} else {
					FacilitiesFeed root = result.getResult();
					stations = root.getStations();
					App.getInstance().getDataBaseHelper().updateStations(stations);
					App.getInstance().getDataBaseHelper().updateTypes(root.getStyles());
				}
				setListAdapter(new StationAdapter(MainActivity.this, stations));
			};
		}.execute("http://www.tfl.gov.uk/assets/downloads/businessandpartners/StationFacilitiessample.xml");
	}

	private static class DownloadFilesTask extends AsyncTask<String, Integer, AsyncTaskResult<FacilitiesFeed>> {
		protected AsyncTaskResult<FacilitiesFeed> doInBackground(String... urls) {
			if (urls.length != 1 || urls[0] == null) {
				throw new IllegalArgumentException("Too many urls, only one is handled, and must be one!");
			}
			try {
				URL url = new URL(urls[0]);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				try {
					connection.connect();
					InputStream input = connection.getInputStream();

					FacilitiesFeed root = new FacilitiesFeedHandler().parse(input);
					return new AsyncTaskResult<FacilitiesFeed>(root);
				} finally {
					connection.disconnect();
				}
			} catch (IOException ex) {
				return new AsyncTaskResult<FacilitiesFeed>(ex);
			}
		}
	}
}
