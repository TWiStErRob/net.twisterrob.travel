package net.twisterrob.blt.android.ui.activity;

import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.model.StopType;
import net.twisterrob.travel.map.MapUtils;

public class StationMapsActivity extends FragmentActivity {
	private GoogleMap map;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(net.twisterrob.blt.android.component.map.R.layout.inc_map);

		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
				.findFragmentById(net.twisterrob.blt.android.component.map.R.id.view__map);
		mapFragment.getMapAsync(new OnMapReadyCallback() {
			@Override public void onMapReady(@NonNull GoogleMap map) {
				StationMapsActivity.this.map = map;
				MapUtils.setMyLocationEnabledIfPossible(StationMapsActivity.this, map);
				LatLng cityOfLondon = new LatLng(51.512161, -0.090981);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(cityOfLondon, 13));
				new LoadStationsTask().execute();
			}
		});
	}

	@SuppressLint("StaticFieldLeak") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	private class LoadStationsTask extends AsyncTask<Void, Void, List<Station>> {
		public void execute() {
			execute((Void[])null);
		}
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
