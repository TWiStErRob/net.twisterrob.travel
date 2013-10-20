package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;
import net.twisterrob.blt.model.Station;
import android.app.ListActivity;
import android.os.Bundle;

public class StationListActivity extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		List<Station> stations = App.getInstance().getDataBaseHelper().getStations();
		setListData(stations);
	}

	protected void setListData(List<Station> stations) {
		Collections.sort(stations, Station.COMPARATOR_NAME);
		setListAdapter(new StationAdapter(this, stations));
	}
}
