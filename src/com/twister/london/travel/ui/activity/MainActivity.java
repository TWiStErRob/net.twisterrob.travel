package com.twister.london.travel.ui.activity;

import java.net.MalformedURLException;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.twister.android.utils.concurrent.AsyncTaskResult;
import com.twister.android.utils.log.*;
import com.twister.london.travel.App;
import com.twister.london.travel.io.feeds.*;
import com.twister.london.travel.io.feeds.android.DownloadFeedTask;
import com.twister.london.travel.model.*;
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
		new DownloadFeedTask<FacilitiesFeed>() {
			protected void onPostExecute(AsyncTaskResult<FacilitiesFeed> result) {
				List<Station> stations;
				if (result.getError() != null) {
					LOG.error("Cannot load facitilies", result.getError());
					Toast.makeText(getApplicationContext(), "Cannot load facitilies" + result.getError().getMessage(),
							Toast.LENGTH_LONG);
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
		}.execute(Feed.StationFacilities);
	}
}
