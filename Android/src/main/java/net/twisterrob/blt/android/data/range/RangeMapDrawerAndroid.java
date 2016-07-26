package net.twisterrob.blt.android.data.range;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.*;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.*;

import net.twisterrob.blt.android.db.model.NetworkNode;

@NotThreadSafe
public class RangeMapDrawerAndroid extends RangeMapDrawer<Bitmap> {
	private static final Logger LOG = LoggerFactory.getLogger(RangeMapDrawerAndroid.class);

	public RangeMapDrawerAndroid(Iterable<NetworkNode> nodes, RangeMapDrawerConfig config) {
		super(nodes, config);
	}

	@Override protected Bitmap createMap(int[] pixels) {
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, (pixelHeight - 1) * pixelWidth, -pixelWidth, 0, 0, pixelWidth, pixelHeight);
		return bitmap;
	}

	public LatLngBounds getBounds() {
		return new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
	}
}