package net.twisterrob.blt.android.ui.activity;

import java.io.IOException;
import java.util.List;

import net.twisterrob.android.map.BaseItemizedOverlay;
import net.twisterrob.android.utils.model.LocationUtils;
import net.twisterrob.android.utils.tools.IOTools;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.db.model.Station;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;

import com.google.android.maps.*;

public class StationMapActivity extends MapActivity {
	protected MapView m_map;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations_map);
		m_map = (MapView)findViewById(R.id.mapview);
		m_map.displayZoomControls(true);
		m_map.setBuiltInZoomControls(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new AsyncTask<Void, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Void... params) {
				try {
					// TODO get from stop
					return IOTools.getImage("http://www.tfl.gov.uk/tfl-global/images/syndication/roundel-tube.png",
							true);
				} catch (IOException ex) {
					ex.printStackTrace();
					return null;
				}
			}
			@Override
			protected void onPostExecute(Bitmap result) {
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
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private final class BaseItemizedOverlayExtension extends BaseItemizedOverlay<Item> {
		private final List<Station> m_stations;
		private Drawable m_drawable;

		protected BaseItemizedOverlayExtension(Drawable defaultMarker, List<Station> stations) {
			super(defaultMarker);
			m_stations = stations;
			Drawable drawable = getResources().getDrawable(R.drawable.tfl_roundel_lul_map);
			drawable.setLevel(1); // TODO based on map level
			m_drawable = BaseItemizedOverlay.bindCenter(drawable);
			populate();
		}
		@Override
		protected Item createItem(int arg0) {
			Item item = new Item(m_stations.get(arg0));
			item.setMarker(m_drawable);
			return item;
		}
		@Override
		public int size() {
			return m_stations.size();
		}
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			if (!shadow) {
				super.draw(canvas, mapView, shadow);
			}
		}
	}

	public static class Item extends OverlayItem {
		public Item(Station station) {
			super(LocationUtils.toGeoPoint(station.getLocation()), station.getName(), station.getAddress());
		}
	}
}
