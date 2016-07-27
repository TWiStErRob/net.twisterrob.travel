package net.twisterrob.blt.android.data.range.tiles;

import java.util.*;

import android.graphics.*;
import android.graphics.Paint.*;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.util.Pools;
import android.text.TextPaint;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.model.LineColors;
import net.twisterrob.java.model.Location;

public class TubeMapTileProvider extends DebugGeneratedGeoTileProvider {
	private final Pools.Pool<TextPaint> textPaints = new Pools.SynchronizedPool<>(10);
	private final TextPaint textPaintProto;
	private final Pools.Pool<Paint> linePaints = new Pools.SynchronizedPool<>(10);
	private final Paint linePaintProto;
	private final Pools.Pool<Paint> lineShadowPaints = new Pools.SynchronizedPool<>(10);
	private final Paint lineShadowPaintProto;
	private final Set<NetworkLink> links;

	public TubeMapTileProvider(Set<NetworkNode> nodes, int tileSize) {
		super(tileSize);
		this.links = getLinks(nodes);

		this.textPaintProto = new TextPaint();
		textPaintProto.setTextAlign(Align.CENTER);
		textPaintProto.setTextSize(tileSize * 0.05f);
		textPaintProto.setColor(Color.BLACK);

		this.linePaintProto = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaintProto.setStyle(Style.STROKE);
		linePaintProto.setStrokeCap(Cap.ROUND);
		linePaintProto.setColor(Color.RED);
		linePaintProto.setStrokeWidth(tileSize * 0.006f);

		this.lineShadowPaintProto = new Paint(Paint.ANTI_ALIAS_FLAG);
		lineShadowPaintProto.setStyle(Style.STROKE);
		linePaintProto.setStrokeCap(Cap.ROUND);
		lineShadowPaintProto.setColor(ColorUtils.setAlphaComponent(Color.WHITE, 0x88));
		// wider than the normal stroke, but at maximum few pixels wider
		float maxWidth = linePaintProto.getStrokeWidth() + tileSize * 0.005f * 2;
		lineShadowPaintProto.setStrokeWidth(
				Math.min(maxWidth, linePaintProto.getStrokeWidth() * 1.5f));
	}

	@Override protected void drawGeoTile(Canvas canvas, int x, int y, int zoom,
			double minLat, double minLon, double maxLat, double maxLon) {
		//super.drawGeoTile(canvas, x, y, zoom, minLat, minLon, maxLat, maxLon);
		TextPaint textPaint = getTextPaint(textPaints, textPaintProto);
		Paint linePaint = getPaint(linePaints, linePaintProto);
		Paint lineShadowPaint = getPaint(lineShadowPaints, lineShadowPaintProto);
		try {
			// see net.twisterrob.blt.android.data.range.tiles.GeoTileProvider.getGeoTile()
			Matrix flipUp = new Matrix();
			flipUp.setScale(1, -1, canvas.getWidth() / 2f, canvas.getHeight() / 2f);
			canvas.setMatrix(flipUp);

			// Simply draw everything for every tile, PNG compressions is much slower than anyway;
			// so unless that's improved there's not much point doing http://stackoverflow.com/q/17133213/253468
			for (NetworkLink link : links) {
				drawLine(canvas, link, minLat, minLon, maxLat, maxLon, linePaint, lineShadowPaint);
			}
		} finally {
			textPaints.release(textPaint);
		}
	}

	private void drawLine(Canvas canvas, NetworkLink link,
			double minLat, double minLon, double maxLat, double maxLon, Paint linePaint, Paint lineShadowPaint) {
		int tileSize = getTileSize();
		Location from = link.getSource().getLocation();
		Location to = link.getTarget().getLocation();
		double p1x = (from.getLongitude() - minLon) / (maxLon - minLon) * tileSize;
		double p2x = (to.getLongitude() - minLon) / (maxLon - minLon) * tileSize;
		double p1y = (from.getLatitude() - minLat) / (maxLat - minLat) * tileSize;
		double p2y = (to.getLatitude() - minLat) / (maxLat - minLat) * tileSize;
		LineColors lineColors = App.getInstance().getStaticData().getLineColors();
		int lineColor = link.getSource().getLine().getBackground(lineColors);
		linePaint.setColor(lineColor);
		canvas.drawLine((float)p1x, (float)p1y, (float)p2x, (float)p2y, lineShadowPaint);
		canvas.drawLine((float)p1x, (float)p1y, (float)p2x, (float)p2y, linePaint);
	}

	private static Set<NetworkLink> getLinks(Set<NetworkNode> nodes) {
		Set<NetworkLink> links = new HashSet<>();
		for (NetworkNode node : nodes) {
			links.addAll(node.getOut());
		}
		return links;
	}
}
