package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.ui.activity.MainActivity.LauncherAdapter.LauncherViewHolder;
import net.twisterrob.blt.model.Line;
import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {

	private static final List<LauncherItem> launcherItems = Arrays.asList( //
			new LauncherItem(R.string.launcher_linestatus, StatusActivity.class), //
			new LauncherItem(R.string.launcher_stationlist, StationListActivity.class), //
			new LauncherItem(R.string.launcher_prediction_summary, PredictionSummaryActivity.class) {
				@Override
				void addIntentParams(Intent intent) {
					super.addIntentParams(intent);
					intent.putExtra(PredictionSummaryActivity.EXTRA_LINE, Line.Central);
				}
			}, //
			new LauncherItem(R.string.launcher_stationmap, StationMapActivity.class) //
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

	protected static class LauncherItem {
		private int m_titleResource;
		private Class<? extends Activity> m_targetActivityClass;

		public LauncherItem(int titleResource, Class<? extends Activity> targetActivityClass) {
			m_titleResource = titleResource;
			m_targetActivityClass = targetActivityClass;
		}

		public int getTitle() {
			return m_titleResource;
		}
		public String getTitle(Context context) {
			return context.getString(m_titleResource);
		}

		Intent createIntent(Context context) {
			Intent intent = new Intent(context, m_targetActivityClass);
			addIntentParams(intent);
			return intent;
		}
		/**
		 * @param intent to add params to
		 */
		void addIntentParams(Intent intent) {
			// optional @Override
		}
	}

	protected static class LauncherAdapter extends BaseListAdapter<LauncherItem, LauncherViewHolder> {
		protected LauncherAdapter(Context context, Collection<LauncherItem> items) {
			super(context, items);
		}

		public static class LauncherViewHolder {
			protected TextView title;
		}

		@Override
		protected int getItemLayoutId() {
			return R.layout.item_main_launcher;
		}
		@Override
		protected LauncherViewHolder createHolder(View convertView) {
			LauncherViewHolder holder = new LauncherViewHolder();
			holder.title = (TextView)convertView.findViewById(R.id.main_launcher_title);
			return holder;
		}
		@Override
		protected void bindView(LauncherViewHolder holder, LauncherItem currentItem, View convertView) {
			holder.title.setText(currentItem.getTitle());
		}
	}

}
