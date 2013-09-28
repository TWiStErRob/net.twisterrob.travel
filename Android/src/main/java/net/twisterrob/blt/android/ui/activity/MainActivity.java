package net.twisterrob.blt.android.ui.activity;

import java.util.List;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.io.feeds.DownloadFeedTask;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.Station;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;

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
							Toast.LENGTH_LONG).show();
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
