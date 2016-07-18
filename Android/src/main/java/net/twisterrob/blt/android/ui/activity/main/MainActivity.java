package net.twisterrob.blt.android.ui.activity.main;

import java.util.*;

import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.ui.activity.*;
import net.twisterrob.blt.model.Line;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.*;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.*;
import android.support.v7.widget.SearchView;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends AppCompatActivity {

	private static final List<LauncherItem> launcherItems = Arrays.asList( //
			new LauncherItem(R.string.launcher_line_status, StatusActivity.class), //
			new LauncherItem(R.string.launcher_station_list, StationListActivity.class), //
			new LauncherItem(R.string.launcher_prediction_summary, PredictionSummaryActivity.class) {
				@Override
				void addIntentParams(Intent intent) {
					super.addIntentParams(intent);
					intent.putExtra(PredictionSummaryActivity.EXTRA_LINE, Line.District);
				}
			}, //
			new LauncherItem(R.string.launcher_station_map, StationMapActivity.class), //
			new LauncherItem(R.string.launcher_station_info, StationInfoActivity.class) {
				@Override
				void addIntentParams(Intent intent) {
					super.addIntentParams(intent);
					intent.putExtra(StationInfoActivity.EXTRA_STATION_NAME, "King's Cross St.Pancras");
				}
			}, //
			new LauncherItem(R.string.launcher_station_map_v2, StationMapsV2Activity.class),//
			new LauncherItem(R.string.launcher_postcode_map, PostCodesActivity.class), //
			new LauncherItem(R.string.launcher_distance_map, DistanceMapActivity.class) //
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

		SearchView searchView = (SearchView)MenuItemCompat.getActionView(menu.findItem(R.id.menu$options$search));
		setSearchableInfo(searchView);

		return super.onCreateOptionsMenu(menu);
	}

	@TargetApi(VERSION_CODES.FROYO)
	protected void setSearchableInfo(SearchView searchView) {
		if (VERSION_CODES.FROYO <= VERSION.SDK_INT) {
			SearchManager searchManager = (SearchManager)getSystemService(SEARCH_SERVICE);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		}
	}

}
