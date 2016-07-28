package net.twisterrob.blt.android.data.range.tiles;

import java.util.Locale;

import android.graphics.*;
import android.graphics.Paint.*;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.util.Pools;
import android.text.TextPaint;

import net.twisterrob.android.utils.tools.ColorTools;
import net.twisterrob.blt.android.BuildConfig;
import net.twisterrob.java.annotations.DebugHelper;

public class DebugGeneratedGeoTileProvider extends GeneratedGeoTileProvider implements MarkerAdder {
	private final Pools.Pool<TextPaint> textPaints = new Pools.SynchronizedPool<>(10);
	private final TextPaint textPaintProto;
	private final Pools.Pool<Paint> pointPaints = new Pools.SynchronizedPool<>(10);
	private final Paint pointPaintProto;

	public DebugGeneratedGeoTileProvider(int tileSize) {
		super(tileSize);
		textPaintProto = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
		textPaintProto.setTextAlign(Align.CENTER);
		textPaintProto.setTextSize(tileSize * 0.04f);
		textPaintProto.setColor(Color.BLACK);
		pointPaintProto = new Paint(Paint.ANTI_ALIAS_FLAG);
		pointPaintProto.setColor(Color.RED);
		pointPaintProto.setStrokeWidth(5);
		pointPaintProto.setStyle(Style.FILL_AND_STROKE);
	}

	@Override protected void drawGeoTile(Canvas canvas, int x, int y, int zoom, double minLat, double minLon,
			double maxLat, double maxLon) {
		LOG.debug("getGeoTile({},{} @ {}): {},{} - {},{}", x, y, zoom, minLat, minLon, maxLat, maxLon);
		TextPaint textPaint = getTextPaint(textPaints, textPaintProto);
		Paint pointPaint = getPaint(pointPaints, pointPaintProto);
		try {
			String text = String.format(Locale.ROOT, "x=%d y=%d z=%d", x, y, zoom);
			Rect textBounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), textBounds);
			canvas.drawColor(ColorUtils.setAlphaComponent(ColorTools.randomColor(), 0x11));
			int textHeight = textBounds.height();
			canvas.drawText(text, canvas.getWidth() / 2, textHeight, textPaint);
			double midLon = (minLon + maxLon) / 2;
			double midLat = (minLat + maxLat) / 2;
			double px = (midLon - minLon) / (maxLon - minLon) * getTileSize();
			double py = (midLat - minLat) / (maxLat - minLat) * getTileSize();
			canvas.drawPoint((float)px, (float)py, pointPaint);
			int line = -3;
			canvas.drawText(String.format("%s", minLon), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(String.format("%s", minLat), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(String.format("%s", midLon), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(String.format("%s", midLat), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(String.format("%s", maxLon), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(String.format("%s", maxLat), (float)px, (float)py + ++line * textHeight, textPaint);

//			String fullDetails = String.format(Locale.ROOT, "%d,%d @ %d; %s,%s - %s,%s (mid = %s,%s)",
//					x, y, zoom, minLon, minLat, maxLon, maxLat, midLon, midLat);
		} finally {
			textPaints.release(textPaint);
			pointPaints.release(pointPaint);
		}
	}

	private MarkerAdder markers;

	@DebugHelper
	@Override public void addMarker(double lat, double lon, String text) {
		if (BuildConfig.DEBUG && markers != null) {
			markers.addMarker(lat, lon, text);
		} else {
			throw new IllegalStateException("This method should not be called in production.");
		}
	}

	public void setMarkers(MarkerAdder markers) {
		this.markers = markers;
	}
}