package net.twisterrob.blt.android.data.range;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import androidx.annotation.NonNull;

import net.twisterrob.blt.android.db.model.NetworkNode;

@NotThreadSafe
public class RangeMapDrawerAndroid extends RangeMapDrawer<Bitmap> {
	private static final Logger LOG = LoggerFactory.getLogger(RangeMapDrawerAndroid.class);

	@SuppressLint("LambdaLast")
	public RangeMapDrawerAndroid(
			@NonNull Iterable<NetworkNode> nodes,
			@NonNull RangeMapDrawerConfig config
	) {
		super(nodes, config, new AndroidOpenGLRenderedGeoSize());
	}

	@Override protected Bitmap createMap(int[] pixels) {
		Bitmap bitmap = Bitmap.createBitmap(size.getPixelWidth(), size.getPixelHeight(), Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, (size.getPixelHeight() - 1) * size.getPixelWidth(), -size.getPixelWidth(), 0, 0,
				size.getPixelWidth(),
				size.getPixelHeight());
		return bitmap;
	}

	public LatLngBounds getBounds() {
		return new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
	}
}
