package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import android.os.*;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.model.StopType;

public class StationMapsV2Activity extends FragmentActivity {
	private GoogleMap m_map;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations_map_v2);

		m_map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		m_map.setMyLocationEnabled(true);
		m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.512161, -0.090981), 13)); // City of london
	}

	@Override protected void onStart() {
		super.onStart();
		new AsyncTask<Void, Void, List<Station>>() {
			@Override protected List<Station> doInBackground(Void... params) {
				return App.getInstance().getDataBaseHelper().getStations();
			}
			@Override protected void onPostExecute(List<Station> result) {
				super.onPostExecute(result);

				@SuppressWarnings("synthetic-access")
				GoogleMap map = m_map;
				Map<StopType, Integer> icons = App.getInstance().getStaticData().getStopTypeMapIcons();
				for (Station station : result) {
					map.addMarker(new MarkerOptions()
							.position(LocationUtils.toLatLng(station.getLocation()))
							.title(station.getName())
							.snippet(station.getAddress())
							.anchor(0.5f, 0.5f)
							.icon(BitmapDescriptorFactory.fromResource(icons.get(station.getType())))
					);
				}
			}
		}.execute((Void[])null);
	}
}
