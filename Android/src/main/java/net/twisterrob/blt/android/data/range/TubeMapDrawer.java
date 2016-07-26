package net.twisterrob.blt.android.data.range;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.*;

import android.graphics.*;

import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.java.model.Location;

// CONSIDER converting to TileProvider
// http://stackoverflow.com/q/17133213/253468: line segments VS rectangle
@NotThreadSafe
public class TubeMapDrawer {
	private static final Logger LOG = LoggerFactory.getLogger(TubeMapDrawer.class);

	protected final double minLon;
	protected final double maxLon;
	protected final double minLat;
	protected final double maxLat;
	protected final double geoWidth;
	protected final double geoHeight;

	protected int pixelWidth;
	protected int pixelHeight;

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
		this.geoWidth = maxLon - minLon;
		this.geoHeight = maxLat - minLat;
		double defaultPixelSize = 1 / Math.max(geoWidth, geoHeight);
		setRenderSafeGeoPixelSize(defaultPixelSize * 2048, defaultPixelSize * 2048);
	}

	/**
	 * Sets the size of a 1° × 1° are of the geo-coordinate system in pixels.
	 */
	public void setGeoPixelSize(double geoLonPixelWidth, double getLatPixelHeight) {
		// *2 makes the pixels square, because the geo coordinate system is twice as wide as tall
		int pixelWidth = (int)(geoWidth * geoLonPixelWidth);
		int pixelHeight = (int)(geoHeight * getLatPixelHeight * 2);
		setPixelSize(pixelWidth, pixelHeight);
	}

	public void setRenderSafeGeoPixelSize(double geoLonPixelWidth, double getLatPixelHeight) {
		// *2 makes the pixels square, because the geo coordinate system is twice as wide as tall
		int pixelWidth = (int)(geoWidth * geoLonPixelWidth);
		int pixelHeight = (int)(geoHeight * getLatPixelHeight * 2);
		setRenderSafePixelSize(pixelWidth, pixelHeight);
	}

	/**
	 * Sets the size of the resulting image in pixels.
	 */
	public void setPixelSize(int imageWidth, int imageHeight) {
		this.pixelWidth = imageWidth;
		this.pixelHeight = imageHeight;
	}

	public void setRenderSafePixelSize(int pixelWidth, int pixelHeight) {
		Point size = AndroidTools.getMaximumBitmapSize(null);
		if (pixelWidth <= size.x && pixelHeight <= size.y) {
			setPixelSize(pixelWidth, pixelHeight);
		} else {
			final float widthPercentage = size.x / (float)pixelWidth;
			final float heightPercentage = size.y / (float)pixelHeight;
			final float minPercentage = Math.min(widthPercentage, heightPercentage);
			setPixelSize((int)(minPercentage * pixelWidth), (int)(minPercentage * pixelHeight));
			LOG.debug("Bitmap size ({}x{}) exceeded OpenGL maximum texture size ({}x{}), scaled down to {}x{}.",
					pixelWidth, pixelHeight, size.x, size.y, this.pixelWidth, this.pixelHeight);
		}
	}

	public Bitmap draw(Set<NetworkNode> nodes) {
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas, getLinks(nodes));
		return bitmap;
	}

	private void draw(Canvas canvas, Set<NetworkLink> links) {
		for (NetworkLink link : links) {
			draw(canvas, link);
		}
	}

	private void draw(Canvas canvas, NetworkLink link) {
		Location from = link.getSource().getLocation();
		Location to = link.getTarget().getLocation();
		double fromX = (from.getLongitude() - minLon) / geoWidth * pixelWidth;
		double fromY = (maxLat - from.getLatitude()) / geoHeight * pixelHeight;
		double toX = (to.getLongitude() - minLon) / geoWidth * pixelWidth;
		double toY = (maxLat - to.getLatitude()) / geoHeight * pixelHeight;

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
