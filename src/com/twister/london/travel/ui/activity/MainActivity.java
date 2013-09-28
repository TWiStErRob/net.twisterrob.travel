package com.twister.london.travel.ui.activity;

import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.twister.android.utils.concurrent.AsyncTaskResult;
import com.twister.london.travel.App;
import com.twister.london.travel.io.feeds.*;
import com.twister.london.travel.io.feeds.android.DownloadFeedTask;
import com.twister.london.travel.model.Station;
import com.twister.london.travel.ui.adapter.StationAdapter;

public class MainActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		delayedGetRoot();
	}

	private void delayedGetRoot() {
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
