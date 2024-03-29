package net.twisterrob.blt.android.ui.activity;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.db.model.AreaHullPoint;
import net.twisterrob.java.model.Location;
import net.twisterrob.travel.map.MapUtils;

public class PostCodesActivity extends FragmentActivity {
	private GoogleMap map;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(net.twisterrob.blt.android.component.map.R.layout.inc_map);

		SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
				.findFragmentById(net.twisterrob.blt.android.component.map.R.id.view__map);
		mapFragment.getMapAsync(new OnMapReadyCallback() {
			@Override public void onMapReady(@NonNull GoogleMap map) {
				PostCodesActivity.this.map = map;
				MapUtils.setMyLocationEnabledIfPossible(PostCodesActivity.this, map);
				LatLng cityOfLondon = new LatLng(51.512161, -0.090981);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(cityOfLondon, 13));
				new LoadPostCodesTask().execute();
			}
		});
	}

	@SuppressLint("StaticFieldLeak") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
	private class LoadPostCodesTask extends AsyncTask<Void, Void, Map<String, List<AreaHullPoint>>> {
		public void execute() {
			execute((Void[])null);
		}
		@Override protected Map<String, List<AreaHullPoint>> doInBackground(Void... params) {
			return App.db().getAreas();
		}
		@Override protected void onPostExecute(Map<String, List<AreaHullPoint>> result) {
			super.onPostExecute(result);

			float[] hsv = {0, 1, 1};
			float[] hues = {
					BitmapDescriptorFactory.HUE_RED,
					BitmapDescriptorFactory.HUE_ORANGE,
					BitmapDescriptorFactory.HUE_YELLOW,
					BitmapDescriptorFactory.HUE_GREEN,
					BitmapDescriptorFactory.HUE_CYAN,
					BitmapDescriptorFactory.HUE_AZURE,
					BitmapDescriptorFactory.HUE_BLUE,
					BitmapDescriptorFactory.HUE_VIOLET,
					BitmapDescriptorFactory.HUE_MAGENTA,
					BitmapDescriptorFactory.HUE_ROSE
			};
			for (Entry<String, List<AreaHullPoint>> area : result.entrySet()) {
				PolygonOptions poly = new PolygonOptions();
				float hue = hues[Math.abs(area.getKey().hashCode()) % hues.length];
				hsv[0] = hue;
				poly.fillColor(Color.HSVToColor(0x40, hsv));
				boolean first = true;
				for (AreaHullPoint point : area.getValue()) {
					Location loc = point.getLocation();
					if (first) {
						first = false;
						map.addMarker(new MarkerOptions()
								.position(LocationUtils.toLatLng(loc))
								.title(area.getKey())
								.icon(BitmapDescriptorFactory.defaultMarker(hue))
						);
					} else {
						poly.add(LocationUtils.toLatLng(loc));
					}
				}
				map.addPolygon(poly);
			}
		}
	}
}
