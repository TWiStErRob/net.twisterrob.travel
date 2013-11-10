package net.twisterrob.blt.android.ui.activity;

import java.util.Map;

import net.twisterrob.android.utils.model.LocationUtils;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.model.Line;
import android.graphics.Bitmap;
import android.os.*;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class DistanceMapActivity extends FragmentActivity {
	private GoogleMap m_map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distance_map);

		m_map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		m_map.setMyLocationEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new AsyncTask<Void, Void, Map<Integer, NetworkNode>>() {
			@Override
			protected Map<Integer, NetworkNode> doInBackground(Void... params) {
				return App.getInstance().getDataBaseHelper().getTubeNetwork();
			}
			@Override
			protected void onPostExecute(Map<Integer, NetworkNode> nodes) {
				super.onPostExecute(nodes);

				@SuppressWarnings("synthetic-access")
				GoogleMap map = m_map;
				//for (NetworkNode node: nodes.values()) {
				//	LatLng ll = LocationUtils.toLatLng(node.getPos());
				//	map.addMarker(new MarkerOptions().title(String.valueOf(node.getID())).position(ll));
				//}
				LatLngBounds bounds = getBounds(nodes.values());
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
				map.moveCamera(cu);
				NetworkNode startNode = nodes.get("Liverpool Street".hashCode());
				map.addMarker(new MarkerOptions() //
						.title("Liverpool Street") //
						.position(LocationUtils.toLatLng(startNode.getPos())) //
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				Bitmap overlay = new DistanceMapGenerator(nodes, new NetworkLink(startNode, Line.Central, 0),
						new DistanceMapConfig() //
								.dynamicColor(true) //
								.blendColors(true)).generate(35);
				map.addGroundOverlay(new GroundOverlayOptions() //
						.positionFromBounds(bounds) //
						.transparency(0.0f) //
						.image(BitmapDescriptorFactory.fromBitmap(overlay)));

			}

			protected LatLngBounds getBounds(Iterable<NetworkNode> nodes) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (NetworkNode node: nodes) {
					LatLng ll = LocationUtils.toLatLng(node.getPos());
					builder.include(ll);
				}
				LatLngBounds bounds = builder.build();
				return bounds;
			}
		}.execute((Void[])null);
	}
}
