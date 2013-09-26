package com.twister.london.travel.ui.activity;

import java.io.*;
import java.net.*;
import java.util.List;

import org.xml.sax.SAXException;

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
		new DownloadFilesTask() {
			protected void onPostExecute(AsyncTaskResult<FacilitiesFeed> result) {
				List<Station> stations;
				if (result.getError() != null) {
					LOG.error("Cannot load facitilies", result.getError());
					stations = App.getInstance().getDataBaseHelper().getStations();
				} else {
					FacilitiesFeed root = result.getResult();
					if (root != null) {
						stations = root.getStations();
						App.getInstance().getDataBaseHelper().updateTypes(root.getStyles());
						App.getInstance().getDataBaseHelper().updateStations(stations);
					} else {
						stations = App.getInstance().getDataBaseHelper().getStations();
					}
				}
				setListAdapter(new StationAdapter(MainActivity.this, stations));
			};
		}.execute(App.getInstance().getUrls().getFeedUrl(Feed.StationFacilities));
	}

	private static class DownloadFilesTask extends AsyncTask<URL, Integer, AsyncTaskResult<FacilitiesFeed>> {
		protected AsyncTaskResult<FacilitiesFeed> doInBackground(URL... urls) {
			if (urls.length != 1 || urls[0] == null) {
				throw new IllegalArgumentException("Too many urls, only one is handled, and must be one!");
			}
			try {
				URL url = urls[0];
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
			} catch (SAXException ex) {
				return new AsyncTaskResult<FacilitiesFeed>(ex);
			}
		}
	}
}
