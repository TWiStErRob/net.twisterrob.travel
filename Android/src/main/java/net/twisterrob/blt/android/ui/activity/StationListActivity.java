package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;

import org.slf4j.*;

import android.app.SearchManager;
import android.content.Intent;
import android.os.*;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.view.Menu;
import android.widget.Filter.FilterListener;
import android.widget.*;

public class StationListActivity extends ActionBarActivity {
	private static final Logger LOG = LoggerFactory.getLogger(StationListActivity.class);

	private ListView m_list;

	private StationAdapter m_adapter;

	private String m_lastFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_stations);

		m_list = (ListView)findViewById(android.R.id.list);

		new AsyncTask<Void, Void, List<Station>>() {
			@Override
			protected List<Station> doInBackground(Void... params) {
				return App.getInstance().getDataBaseHelper().getStations();
			}
			@Override
			protected void onPostExecute(List<Station> result) {
				populateListData(result);
			}
		}.execute();

		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		LOG.debug("handleIntent: {}", intent);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			if (query == null) {
				query = ((SpannableString)intent.getExtras().get(SearchManager.USER_QUERY)).toString();
			}
			filter(query);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_stations_list, menu);

		SearchView searchView = (SearchView)MenuItemCompat.getActionView(menu.findItem(R.id.menu$options$search));
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			public boolean onQueryTextSubmit(String query) {
				filter(query);
				return true;
			}

			public boolean onQueryTextChange(String newText) {
				filter(newText);
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	protected void filter(String query) {
		m_lastFilter = query;
		LOG.debug("filter/query: {}", query);
		if (m_adapter == null) {
			return;
		}
		m_adapter.getFilter().filter(query, new FilterListener() {
			@SuppressWarnings("hiding") private final Logger LOG = LoggerFactory.getLogger(getClass());
			public void onFilterComplete(int count) {
				LOG.debug("Filtered: {}", count);
			}
		});
	}

	protected void populateListData(List<Station> stations) {
		Collections.sort(stations, Station.COMPARATOR_NAME);
		m_adapter = new StationAdapter(this, stations);
		filter(m_lastFilter);
		m_list.setAdapter(m_adapter);
	}
}
