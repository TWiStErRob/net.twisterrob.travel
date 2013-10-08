package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.Station;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;

public class StationListActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<Station> stations = App.getInstance().getDataBaseHelper().getStations();
		if (stations == null || stations.isEmpty()) {
			delayedGetRoot();
			return;
		}
		setListData(stations);
	}

	protected void setListData(List<Station> stations) {
		Collections.sort(stations, Station.COMPARATOR_NAME);
		setListAdapter(new StationAdapter(this, stations));
	}

	private void delayedGetRoot() {
		new DownloadFeedTask<FacilitiesFeed>() {
			protected void onPostExecute(AsyncTaskResult<FacilitiesFeed> result) {
				if (result.getError() != null) {
					String msg = "Cannot load facitilies: " + result.getError().getMessage();
					LOG.error(msg, result.getError());
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
					setListData(Collections.<Station> emptyList());
				} else if (result.getResult() == null) {
					String msg = "No stations returned.";
					LOG.warn(msg);
					Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
					setListData(Collections.<Station> emptyList());
				} else {
					FacilitiesFeed root = result.getResult();
					List<Station> stations = root.getStations();
					App.getInstance().getDataBaseHelper().updateTypes(root.getStyles());
					App.getInstance().getDataBaseHelper().updateStations(stations);
					setListData(stations);
				}
			};
		}.execute(Feed.StationFacilities);
	}
}
