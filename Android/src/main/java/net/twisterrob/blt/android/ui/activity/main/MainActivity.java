package net.twisterrob.blt.android.ui.activity.main;

import java.util.*;

import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.ui.activity.*;
import net.twisterrob.blt.model.Line;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {

	private static final List<LauncherItem> launcherItems = Arrays.asList( //
			new LauncherItem(R.string.launcher_line_status, StatusActivity.class), //
			new LauncherItem(R.string.launcher_station_list, StationListActivity.class), //
			new LauncherItem(R.string.launcher_prediction_summary, PredictionSummaryActivity.class) {
				@Override
				void addIntentParams(Intent intent) {
					super.addIntentParams(intent);
					intent.putExtra(PredictionSummaryActivity.EXTRA_LINE, Line.Central);
				}
			}, //
			new LauncherItem(R.string.launcher_station_map, StationMapActivity.class), //
			new LauncherItem(R.string.launcher_station_info, StationInfoActivity.class) {
				@Override
				void addIntentParams(Intent intent) {
					super.addIntentParams(intent);
					intent.putExtra(StationInfoActivity.EXTRA_STATION_NAME, "King's Cross St. Pancras");
				}
			} //
			);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		GridView grid = (GridView)findViewById(android.R.id.list);
		grid.setAdapter(new LauncherAdapter(this, launcherItems));
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LauncherItem item = (LauncherItem)parent.getItemAtPosition(position);
				Intent intent = item.createIntent(MainActivity.this);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_main, menu);

		View searchView = MenuItemCompat.getActionView(menu.findItem(R.id.menu$options$search));
		SearchViewCompat.setSearchableInfo(searchView, getComponentName());
		SearchViewCompat.setIconified(searchView, false);

		return super.onCreateOptionsMenu(menu);
	}

}
