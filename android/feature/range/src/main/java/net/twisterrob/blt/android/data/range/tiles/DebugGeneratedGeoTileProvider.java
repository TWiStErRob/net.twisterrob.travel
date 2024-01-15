package net.twisterrob.blt.android.data.range.tiles;

import java.util.Locale;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.TextPaint;

import androidx.core.graphics.ColorUtils;
import androidx.core.util.Pools;

import net.twisterrob.android.utils.tools.ColorTools;
import net.twisterrob.java.annotations.DebugHelper;

public class DebugGeneratedGeoTileProvider extends GeneratedGeoTileProvider implements MarkerAdder {
	private final Pools.Pool<TextPaint> textPaints = new Pools.SynchronizedPool<>(10);
	private final TextPaint textPaintProto;
	private final Pools.Pool<Paint> pointPaints = new Pools.SynchronizedPool<>(10);
	private final Paint pointPaintProto;
	private final boolean isDebug;

	public DebugGeneratedGeoTileProvider(int tileSize, boolean isDebug) {
		super(tileSize);
		this.isDebug = isDebug;
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
			canvas.drawText(Double.toString(minLon), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(Double.toString(minLat), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(Double.toString(midLon), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(Double.toString(midLat), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(Double.toString(maxLon), (float)px, (float)py + ++line * textHeight, textPaint);
			canvas.drawText(Double.toString(maxLat), (float)px, (float)py + ++line * textHeight, textPaint);

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
		if (isDebug && markers != null) {
			markers.addMarker(lat, lon, text);
		} else {
			throw new IllegalStateException("This method should not be called in production.");
		}
	}

	public void setMarkers(MarkerAdder markers) {
		this.markers = markers;
	}
}
