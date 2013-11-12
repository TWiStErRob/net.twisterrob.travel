package net.twisterrob.blt.android.data.distance;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.android.db.model.NetworkNode;

import org.slf4j.*;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.*;

@NotThreadSafe
public class DistanceMapDrawerAndroid extends DistanceMapDrawer<Bitmap> {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapDrawerAndroid.class);

	public DistanceMapDrawerAndroid(Iterable<NetworkNode> nodes, DistanceMapDrawerConfig config) {
		super(nodes, config);
	}

	@Override
	protected Bitmap createMap(int[] pixels) {
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, (pixelHeight - 1) * pixelWidth, -pixelWidth, 0, 0, pixelWidth, pixelHeight);
		return bitmap;
	}

	public LatLngBounds getBounds() {
		return new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
	}
}
