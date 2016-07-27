package net.twisterrob.blt.android.data.range;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import android.graphics.*;

import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.java.model.Location;

@NotThreadSafe
public class TubeMapDrawer {
	protected final double minLon;
	protected final double maxLon;
	protected final double minLat;
	protected final double maxLat;
	private final RenderedGeoSize size = new AndroidOpenGLRenderedGeoSize();

	public TubeMapDrawer(Iterable<NetworkNode> nodes) {
		double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for (NetworkNode node : nodes) {
			double lat = node.getLocation().getLatitude();
			double lon = node.getLocation().getLongitude();
			if (lon < minX) {
				minX = lon;
			} else if (maxX < lon) {
				maxX = lon;
			}
			if (lat < minY) {
				minY = lat;
			} else if (maxY < lat) {
				maxY = lat;
			}
		}
		this.minLon = minX;
		this.maxLon = maxX;
		this.minLat = minY;
		this.maxLat = maxY;
		double geoWidth = maxLon - minLon;
		double geoHeight = maxLat - minLat;
		size.setGeoSize(geoWidth, geoHeight);
		double defaultPixelSize = 1 / Math.max(geoWidth, geoHeight);
		size.setSafeGeoPixelSize(defaultPixelSize * 2048, defaultPixelSize * 2048);
	}

	public void setSize(float geoLonPixelWidth, float geoLatPixelHeight) {
		size.setSafeGeoPixelSize(geoLonPixelWidth, geoLatPixelHeight);
	}

	public Bitmap draw(Set<NetworkNode> nodes) {
		Bitmap bitmap = Bitmap.createBitmap(size.getPixelWidth(), size.getPixelHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		double scaleX = 1.0 / size.getGeoLonSpan() * size.getPixelWidth();
		double scaleY = 1.0 / size.getGeoLatSpan() * size.getPixelHeight();
		draw(canvas, getLinks(nodes), scaleX, scaleY);
		return bitmap;
	}

	private void draw(Canvas canvas, Set<NetworkLink> links, double scaleX, double scaleY) {
		for (NetworkLink link : links) {
			draw(canvas, link, scaleX, scaleY);
		}
	}

	private void draw(Canvas canvas, NetworkLink link, double scaleX, double scaleY) {
		Location from = link.getSource().getLocation();
		Location to = link.getTarget().getLocation();
		double fromX = (from.getLongitude() - minLon) * scaleX;
		double fromY = (maxLat - from.getLatitude()) * scaleY;
		double toX = (to.getLongitude() - minLon) * scaleX;
		double toY = (maxLat - to.getLatitude()) * scaleY;

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(link.getSource().getLine().getBackground(App.getInstance().getStaticData().getLineColors()));
		paint.setStrokeWidth(4);
		canvas.drawLine((float)fromX, (float)fromY, (float)toX, (float)toY, paint);
	}

	private static Set<NetworkLink> getLinks(Set<NetworkNode> nodes) {
		Set<NetworkLink> links = new HashSet<>();
		for (NetworkNode node : nodes) {
			links.addAll(node.getOut());
		}
		return links;
	}
}
