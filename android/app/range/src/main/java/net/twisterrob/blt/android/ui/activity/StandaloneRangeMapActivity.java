package net.twisterrob.blt.android.ui.activity;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import net.twisterrob.android.activity.AboutActivity;
import net.twisterrob.blt.android.app.range.R;

public class StandaloneRangeMapActivity extends RangeMapActivity {
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_range_map_standalone, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home
				|| id == R.id.menu__action__about) {
			startActivity(new Intent(getApplicationContext(), AboutActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
