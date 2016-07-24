package net.twisterrob.blt.android.data.distance;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.*;

import android.graphics.*;

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

	protected final int pixelWidth;
	protected final int pixelHeight;

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
		// *2 makes the pixels square, because the geo coordinate system is twice as wide as tall
		this.pixelWidth = (int)(geoWidth * 3000);
		this.pixelHeight = (int)(geoHeight * 3000) * 2;
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
