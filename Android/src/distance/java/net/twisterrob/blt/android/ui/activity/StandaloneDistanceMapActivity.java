package net.twisterrob.blt.android.ui.activity;

import android.content.Intent;
import android.view.*;

import net.twisterrob.android.activity.AboutActivity;
import net.twisterrob.blt.android.R;

public class StandaloneDistanceMapActivity extends DistanceMapActivity {
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.options_distance_map_standalone, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
			case R.id.menu$action$about:
				startActivity(new Intent(getApplicationContext(), AboutActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
