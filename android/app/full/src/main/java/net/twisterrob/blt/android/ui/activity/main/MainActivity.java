package net.twisterrob.blt.android.ui.activity.main;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import net.twisterrob.android.activity.AboutActivity;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.blt.android.app.full.R;
import net.twisterrob.blt.android.ui.activity.BaseActivity;
import net.twisterrob.blt.android.ui.activity.PostCodesActivity;
import net.twisterrob.blt.android.ui.activity.PredictionSummaryActivity;
import net.twisterrob.blt.android.ui.activity.RangeMapActivity;
import net.twisterrob.blt.android.ui.activity.StationInfoActivity;
import net.twisterrob.blt.android.ui.activity.StationListActivity;
import net.twisterrob.blt.android.ui.activity.StationMapsActivity;
import net.twisterrob.blt.android.ui.activity.StatusActivity;
import net.twisterrob.blt.model.Line;

public class MainActivity extends BaseActivity {

	private static final List<LauncherItem> launcherItems = Arrays.asList(
			new LauncherItem(R.string.launcher__line_status, StatusActivity.class),
			new LauncherItem(R.string.launcher__station_list, StationListActivity.class),
			new LauncherItem(R.string.launcher__prediction_summary, PredictionSummaryActivity.class) {
				@Override void addIntentParams(Intent intent) {
					super.addIntentParams(intent);
					intent.putExtra(PredictionSummaryActivity.EXTRA_LINE, Line.District);
				}
			},
			new LauncherItem(R.string.launcher__station_info, StationInfoActivity.class) {
				@Override void addIntentParams(Intent intent) {
					super.addIntentParams(intent);
					intent.putExtra(StationInfoActivity.EXTRA_STATION_NAME, "King's Cross St. Pancras");
				}
			},
			new LauncherItem(R.string.launcher__station_map, StationMapsActivity.class),
			new LauncherItem(R.string.launcher__postcode_map, PostCodesActivity.class),
			new LauncherItem(R.string.launcher__range_map, RangeMapActivity.class)
	);

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		GridView grid = (GridView)findViewById(android.R.id.list);
		grid.setAdapter(new LauncherAdapter(this, launcherItems));
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				LauncherItem item = (LauncherItem)parent.getItemAtPosition(position);
				Intent intent = item.createIntent(MainActivity.this);
				startActivity(intent);
			}
		});
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		AndroidTools.prepareSearch(this, menu, R.id.menu__action__search);
		return super.onCreateOptionsMenu(menu);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu__action__about) {
			startActivity(new Intent(getApplicationContext(), AboutActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
