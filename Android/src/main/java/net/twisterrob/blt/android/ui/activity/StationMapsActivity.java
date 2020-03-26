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

public class StationMapsActivity extends FragmentActivity {
	private GoogleMap map;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inc_map);

		SupportMapFragment mapFragment =
				(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.view__map);
		mapFragment.getMapAsync(new OnMapReadyCallback() {
			@Override public void onMapReady(GoogleMap map) {
				StationMapsActivity.this.map = map;
				map.setMyLocationEnabled(true);
				LatLng cityOfLondon = new LatLng(51.512161, -0.090981);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(cityOfLondon, 13));
				new LoadStationsTask().execute((Void[])null);
			}
		});
	}

	private class LoadStationsTask extends AsyncTask<Void, Void, List<Station>> {
		@Override protected List<Station> doInBackground(Void... params) {
			return App.db().getStations();
		}
		@Override protected void onPostExecute(List<Station> result) {
			super.onPostExecute(result);

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
	}
}
