package net.twisterrob.blt.android.ui.activity;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter.FilterListener;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.app.full.R;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.android.ui.adapter.StationAdapter;

public class StationListActivity extends BaseActivity implements FilterListener {
	private static final Logger LOG = LoggerFactory.getLogger(StationListActivity.class);

	private ListView m_list;

	private StationAdapter m_adapter;

	private String m_lastFilter;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_stations);
		resetToList();
		findViewById(R.id.layout__wrapper).setEnabled(false);

		m_list = (ListView)findViewById(android.R.id.list);
		m_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Station station = (Station)parent.getItemAtPosition(position);
				Intent intent = new Intent(StationListActivity.this, StationInfoActivity.class);
				{
					String stationName = station.getName();
					intent.putExtra(StationInfoActivity.EXTRA_STATION_NAME, stationName);
				}
				startActivity(intent);
			}
		});

		@SuppressLint("StaticFieldLeak") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		@SuppressWarnings({"unused", "deprecation"}) // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		Object task = new AsyncTask<Void, Void, List<Station>>() {
			@Override protected List<Station> doInBackground(Void... params) {
				return App.db().getStations();
			}
			@Override protected void onPostExecute(List<Station> result) {
				populateListData(result);
			}
		}.execute();

		handleIntent(getIntent());
	}

	private void handleIntent(Intent intent) {
		LOG.debug("handleIntent: {}", intent);
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			filter(query);
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			SpannableString querySpan = (SpannableString)intent.getCharSequenceExtra(SearchManager.USER_QUERY);
			String query = querySpan.toString();
			filter(query);
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.stations_list, menu);

		SearchView searchView = (SearchView)menu.findItem(R.id.menu__action__search).getActionView();
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
		m_adapter.getFilter().filter(query, this);
	}

	public void onFilterComplete(int count) {
		LOG.debug("Filtered: {}", count);
		m_adapter.notifyDataSetChanged();
		m_list.setSelection(0);
	}

	protected void populateListData(List<Station> stations) {
		Collections.sort(stations, Station.COMPARATOR_NAME);
		m_adapter = new StationAdapter(this, stations, App.getInstance().getStaticData());
		filter(m_lastFilter);
		m_list.setAdapter(m_adapter);
	}
}
