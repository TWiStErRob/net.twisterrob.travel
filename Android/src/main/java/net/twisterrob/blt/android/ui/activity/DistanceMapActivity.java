package net.twisterrob.blt.android.ui.activity;

import java.util.*;

import net.twisterrob.android.utils.model.LocationUtils;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.data.distance.*;
import net.twisterrob.blt.android.db.model.NetworkNode;
import net.twisterrob.blt.model.Line;

import org.slf4j.*;

import android.graphics.Bitmap;
import android.os.*;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class DistanceMapActivity extends FragmentActivity {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapActivity.class);

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
		new AsyncTask<Void, Void, Set<NetworkNode>>() {
			@Override
			protected Set<NetworkNode> doInBackground(Void... params) {
				return App.getInstance().getDataBaseHelper().getTubeNetwork();
			}
			@Override
			protected void onPostExecute(Set<NetworkNode> nodes) {
				super.onPostExecute(nodes);

				@SuppressWarnings("synthetic-access")
				GoogleMap map = m_map;
				//for (NetworkNode node: nodes.values()) {
				//	LatLng ll = LocationUtils.toLatLng(node.getPos());
				//	map.addMarker(new MarkerOptions().title(String.valueOf(node.getID())).position(ll));
				//}
				LatLngBounds bounds = getBounds(nodes);
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
				map.moveCamera(cu);
				NetworkNode startNode = NetworkNode.find(nodes, "Liverpool Street", Line.Central);
				map.addMarker(new MarkerOptions() //
						.title("Liverpool Street") //
						.position(LocationUtils.toLatLng(startNode.getLocation())) //
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				DistanceMapGeneratorConfig distanceConfig = new DistanceMapGeneratorConfig() //
						.minutes(25);
				DistanceMapDrawerConfig drawConfig = new DistanceMapDrawerConfig() //
						.dynamicColor(true);
				DistanceMapGenerator distanceMapGenerator = new DistanceMapGenerator(nodes, startNode, distanceConfig);
				DistanceMapDrawer distanceMapDrawer = new DistanceMapDrawer(nodes, drawConfig);

				LOG.debug("Neighbors: {}", startNode.neighbors);
				LOG.debug("Dists: {}", startNode.dists);
				Map<NetworkNode, Double> distanceMap = distanceMapGenerator.generate();
				Bitmap overlay = distanceMapDrawer.draw(distanceMap);

				map.addGroundOverlay(new GroundOverlayOptions() //
						.positionFromBounds(bounds) //
						.transparency(0.0f) //
						.image(BitmapDescriptorFactory.fromBitmap(overlay)));

			}

			protected LatLngBounds getBounds(Iterable<NetworkNode> nodes) {
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for (NetworkNode node: nodes) {
					LatLng ll = LocationUtils.toLatLng(node.getLocation());
					builder.include(ll);
				}
				LatLngBounds bounds = builder.build();
				return bounds;
			}
		}.execute((Void[])null);
	}
}
