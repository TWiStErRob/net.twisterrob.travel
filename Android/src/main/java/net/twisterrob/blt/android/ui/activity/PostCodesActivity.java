package net.twisterrob.blt.android.ui.activity;

import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.android.utils.model.LocationUtils;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.db.model.AreaHullPoint;
import net.twisterrob.java.model.Location;
import android.graphics.Color;
import android.os.*;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class PostCodesActivity extends FragmentActivity {
	private GoogleMap m_map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations_map_v2);

		m_map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		m_map.setMyLocationEnabled(true);
		m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.512161, -0.090981), 13)); // City of london
	}

	@Override
	protected void onStart() {
		super.onStart();
		new AsyncTask<Void, Void, Map<String, List<AreaHullPoint>>>() {
			@Override
			protected Map<String, List<AreaHullPoint>> doInBackground(Void... params) {
				return App.getInstance().getDataBaseHelper().getAreas();
			}
			@Override
			protected void onPostExecute(Map<String, List<AreaHullPoint>> result) {
				super.onPostExecute(result);

				@SuppressWarnings("synthetic-access")
				GoogleMap map = m_map;

				float[] hsv = {0, 1, 1};
				float[] hues = {BitmapDescriptorFactory.HUE_RED, //
						BitmapDescriptorFactory.HUE_ORANGE, //
						BitmapDescriptorFactory.HUE_YELLOW, //
						BitmapDescriptorFactory.HUE_GREEN, //
						BitmapDescriptorFactory.HUE_CYAN, //
						BitmapDescriptorFactory.HUE_AZURE, //
						BitmapDescriptorFactory.HUE_BLUE, //
						BitmapDescriptorFactory.HUE_VIOLET, //
						BitmapDescriptorFactory.HUE_MAGENTA, //
						BitmapDescriptorFactory.HUE_ROSE //
				};
				for (Entry<String, List<AreaHullPoint>> area: result.entrySet()) {
					PolygonOptions poly = new PolygonOptions();
					float hue = hues[Math.abs(area.getKey().hashCode()) % hues.length];
					hsv[0] = hue;
					poly.fillColor(Color.HSVToColor(0x40, hsv));
					boolean first = true;
					for (AreaHullPoint point: area.getValue()) {
						Location loc = point.getLocation();
						if (first) {
							first = false;
							map.addMarker(new MarkerOptions() //
									.position(LocationUtils.toLatLng(loc)) //
									.title(area.getKey()) //
									.icon(BitmapDescriptorFactory.defaultMarker(hue)) //
							);
						} else {
							poly.add(LocationUtils.toLatLng(loc));
						}
					}
					map.addPolygon(poly);
				}
			}
		}.execute((Void[])null);
	}
}
