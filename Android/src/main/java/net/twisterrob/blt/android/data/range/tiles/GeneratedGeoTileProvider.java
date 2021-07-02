package net.twisterrob.blt.android.data.range.tiles;

import java.io.ByteArrayOutputStream;

import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import androidx.annotation.*;
import androidx.core.util.Pools;
import android.text.TextPaint;

import com.bumptech.glide.util.Util;
import com.google.android.gms.maps.model.Tile;

public abstract class GeneratedGeoTileProvider extends GeoTileProvider {
	protected GeneratedGeoTileProvider(int tileSize) {
		super(tileSize);
	}

	@Override protected @Nullable Tile getGeoTile(int x, int y, int zoom,
			double minLat, double minLon, double maxLat, double maxLon) {
		long begin = System.currentTimeMillis();
		Bitmap bitmap = createTileBitmap();
		Canvas canvas = new Canvas(bitmap);
		long created = System.currentTimeMillis();
		drawGeoTile(canvas, x, y, zoom, minLat, minLon, maxLat, maxLon);
		long drawn = System.currentTimeMillis();
		Tile tile = toTile(bitmap);
		long converted = System.currentTimeMillis();
		LOG.trace(
				"Rendering tile {},{} @ {} "
						+ "drawing a {}x{} Bitmap ({} bytes) compressed into {} bytes "
						+ "took {} ms (create = {} ms, draw = {} ms, compress = {} ms)",
				x, y, zoom,
				bitmap.getWidth(), bitmap.getHeight(), Util.getBitmapByteSize(bitmap), tile.data.length,
				converted - begin, created - begin, drawn - created, converted - drawn);
		return tile;
	}

	protected abstract void drawGeoTile(Canvas canvas, int x, int y, int zoom,
			double minLat, double minLon, double maxLat, double maxLon);

	protected @NonNull Bitmap createTileBitmap() {
		return Bitmap.createBitmap(getTileSize(), getTileSize(), Bitmap.Config.ARGB_8888);
	}

	protected @NonNull Tile toTile(Bitmap bitmap) {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			// TODO create a uncompressed PNG byte stream from the raw bitmap.getPixels() data
			bitmap.compress(CompressFormat.PNG, 100, bytes);
			return new Tile(bitmap.getWidth(), bitmap.getHeight(), bytes.toByteArray());
		} finally {
			bitmap.recycle();
		}
	}

	protected @NonNull Paint getPaint(Pools.Pool<? extends Paint> pool, Paint protoType) {
		Paint paint = pool.acquire();
		if (paint == null) {
			paint = new Paint(protoType);
		}
		return paint;
	}

	protected @NonNull TextPaint getTextPaint(Pools.Pool<? extends TextPaint> pool, TextPaint protoType) {
		TextPaint textPaint = pool.acquire();
		if (textPaint == null) {
			textPaint = new TextPaint(protoType);
		}
		return textPaint;
	}
}
