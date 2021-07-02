package net.twisterrob.blt.android.ui.activity;

import java.util.*;
import java.util.Map.Entry;

import android.graphics.Color;
import android.os.*;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.data.LocationUtils;
import net.twisterrob.blt.android.db.model.AreaHullPoint;
import net.twisterrob.java.model.Location;

public class PostCodesActivity extends FragmentActivity {
	private GoogleMap map;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inc_map);

		SupportMapFragment mapFragment =
				(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.view__map);
		mapFragment.getMapAsync(new OnMapReadyCallback() {
			@Override public void onMapReady(GoogleMap map) {
				PostCodesActivity.this.map = map;
				map.setMyLocationEnabled(true);
				LatLng cityOfLondon = new LatLng(51.512161, -0.090981);
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(cityOfLondon, 13));
				new LoadPostCodesTask().execute((Void[])null);
			}
		});
	}

	private class LoadPostCodesTask extends AsyncTask<Void, Void, Map<String, List<AreaHullPoint>>> {
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
