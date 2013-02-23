package com.twister.london.travel.ui.activity;

import java.io.IOException;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;

import com.google.android.maps.*;
import com.twister.android.map.BaseItemizedOverlay;
import com.twister.android.utils.tools.IOTools;
import com.twister.london.travel.*;
import com.twister.london.travel.model.*;

public class StationMapActivity extends MapActivity {

	private MapView m_map;

	@Override public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations_map);
		m_map = (MapView)findViewById(R.id.mapview);
		m_map.displayZoomControls(true);
		m_map.setBuiltInZoomControls(true);
	}

	@Override protected void onStart() {
		super.onStart();
		new AsyncTask<Void, Void, Bitmap>() {
			@Override protected Bitmap doInBackground(Void... params) {
				try {
					return IOTools.getImage(Type.Tube.getUrl(), true);
				} catch (IOException ex) {
					ex.printStackTrace();
					return null;
				}
			}
			@Override protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				List<Station> stations = App.getInstance().getDataBaseHelper().getStations();
				Drawable drawable = new BitmapDrawable(getResources(), result);
				BaseItemizedOverlay.bindCenter(drawable);
				BaseItemizedOverlay<Item> object = new BaseItemizedOverlayExtension(drawable, stations);
				m_map.getOverlays().add(object);
				GeoPoint center = new GeoPoint((int)(51.512161 * 1e6), (int)(-0.090981 * 1e6)); // City of
																								// london
				m_map.getController().setCenter(center);
				m_map.getController().setZoom(13);
			}
		}.execute((Void[])null);
	}
	@Override public boolean onCreateOptionsMenu(final Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override protected boolean isRouteDisplayed() {
		return false;
	}

	private final class BaseItemizedOverlayExtension extends BaseItemizedOverlay<Item> {
		private final List<Station> m_stations;

		private BaseItemizedOverlayExtension(Drawable defaultMarker, List<Station> stations) {
			super(defaultMarker);
			m_stations = stations;
			populate();
		}
		@Override protected Item createItem(int arg0) {
			return new Item(getResources(), m_stations.get(arg0));
		}
		@Override public int size() {
			return m_stations.size();
		}
	}

	public static class Item extends OverlayItem {
		private Resources m_res;
		private Station m_station;
		public Item(Resources res, Station station) {
			super(station.getLocation().toGeoPoint(), station.getName(), station.getAddress());
			m_res = res;
			m_station = station;
		}
		@Override public Drawable getMarker(int arg0) {
			Bitmap result;
			try {
				result = IOTools.getImage(m_station.getType().getUrl(), true);
				Drawable drawable = new BitmapDrawable(m_res, result);
				BaseItemizedOverlay.bindCenter(drawable);
				return drawable;
			} catch (IOException ex) {
				// TODO store Type in DB: ex.printStackTrace();
			}
			return super.getMarker(arg0);
		}
	}
}
