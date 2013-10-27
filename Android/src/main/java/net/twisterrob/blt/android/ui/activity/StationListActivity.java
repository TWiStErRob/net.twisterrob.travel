package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;

import org.slf4j.*;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.Filter.FilterListener;
import android.widget.*;

public class StationListActivity extends ActionBarActivity {
	private static final Logger LOG = LoggerFactory.getLogger(StationListActivity.class);

	private ListView m_list;

	private StationAdapter m_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_stations);

		m_list = (ListView)findViewById(android.R.id.list);

		List<Station> stations = App.getInstance().getDataBaseHelper().getStations();
		setListData(stations);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		LOG.debug("handleIntent: {}", intent);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			filter(query);
		}
	}

	protected void filter(String query) {
		LOG.debug("filter/query: {}", query);
		m_adapter.getFilter().filter(query, new FilterListener() {
			@SuppressWarnings("hiding") private final Logger LOG = LoggerFactory.getLogger(getClass());

			public void onFilterComplete(int count) {
				LOG.debug("Filtered: {}", count);
				// TODO clear search UI?
			}
		});
	}
	protected void setListData(List<Station> stations) {
		Collections.sort(stations, Station.COMPARATOR_NAME);
		m_list.setAdapter(m_adapter = new StationAdapter(this, stations));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_stations_list, menu);

		View searchView = MenuItemCompat.getActionView(menu.findItem(R.id.menu$options$search));
		LOG.debug("Got view: {} / {}", searchView != null? searchView.getClass() : null, searchView);
		SearchViewCompat.setSearchableInfo(searchView, getComponentName());
		// SearchViewCompat.setIconified(searchView, false);

		return true;
	}
}
