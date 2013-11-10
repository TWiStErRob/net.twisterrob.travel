package net.twisterrob.blt.android.data.distance;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.android.db.model.NetworkNode;
import net.twisterrob.blt.model.Line;
import net.twisterrob.java.model.*;

import org.slf4j.*;

import android.graphics.*;

@NotThreadSafe
public class DistanceMapDrawer {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapDrawer.class);

	private final DistanceMapDrawerConfig config;

	private final double minLon;
	private final double maxLon;
	private final double minLat;
	private final double maxLat;
	private final double geoWidth;
	private final double geoHeight;

	private final int pixelWidth;
	private final int pixelHeight;
	private int[] pixels;

	public DistanceMapDrawer(Iterable<NetworkNode> nodes, DistanceMapDrawerConfig config) {
		this.config = config;
		double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for (NetworkNode node: nodes) {
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
		this.pixelWidth = (int)(geoWidth * config.pixelDensity);
		this.pixelHeight = (int)(geoHeight * config.pixelDensity) * 2;
	}

	public DistanceMapDrawerConfig getConfig() {
		return config;
	}

	public Bitmap draw(Map<NetworkNode, Double> nodes) {
		calcPixels(nodes);
		if (0 < config.borderSize) {
			border(config.borderSize, config.borderColor);
		}
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, (pixelHeight - 1) * pixelWidth, -pixelWidth, 0, 0, pixelWidth, pixelHeight);
		pixels = null;
		return bitmap;
	}

	private int[] calcPixels(Map<NetworkNode, Double> nodes) {
		//LOG.debug("Mapping area w={}, h={} to pixels w={}, h={}", //
		//		geoWidth, geoHeight, pixelWidth, pixelHeight);
		pixels = new int[pixelHeight * pixelWidth];
		for (Entry<NetworkNode, Double> circle: nodes.entrySet()) {
			drawCircle(circle.getKey(), circle.getValue());
		}
		return pixels;
	}

	private void drawCircle(NetworkNode node, double remainingWalk) {
		double phi = Math.toRadians(node.getLocation().getLatitude());
		double meters_per_lat_degree = LocationConverter.metersPerDegreeLat(phi);
		double meters_per_lon_degree = LocationConverter.metersPerDegreeLon(phi);
		//LOG.debug("Drawing for {} / {} remaining: {}", node.getLine(), node.getName(), (int)remainingWalk);
		drawCircle(node.getLocation(), node.getLine(), remainingWalk / meters_per_lon_degree, remainingWalk
				/ meters_per_lat_degree);
	}

	/**
	 * @param pixels canvas
	 * @param pos center of the "circle"
	 * @param line 
	 * @param widthDegrees width of the circle in geo-degrees (longitude)
	 * @param heightDegrees width of the circle in geo-degrees (latitude)
	 */
	protected void drawCircle(Location pos, Line line, double widthDegrees, double heightDegrees) {
		double nodeXOffset = pos.getLongitude() - minLon;
		double nodeYOffset = pos.getLatitude() - minLat;
		int nodeX = (int)(nodeXOffset / geoWidth * pixelWidth);
		int nodeY = (int)(nodeYOffset / geoHeight * pixelHeight);
		int rX = (int)(widthDegrees / geoWidth * pixelWidth);
		int rY = (int)(heightDegrees / geoHeight * pixelHeight);
		//LOG.debug("Drawing a circle at {},{} for {} (pixels: {},{}, radii: {},{})", //
		//		pos.getLongitude(), pos.getLatitude(), line, nodeX, nodeY, rX, rY);
		drawEllipse(nodeX, nodeY, rX, rY, config.getColor(line));
	}

	/**
	 * @param pixels canvas
	 * @param nodeX center x
	 * @param nodeY center y
	 * @param r radius around center
	 */
	protected void drawCircle(int nodeX, int nodeY, int r, int color) {
		int startX = Math.max(nodeX - r, 0);
		int endX = Math.min(nodeX + r, pixelWidth);
		int startY = Math.max(nodeY - r, 0);
		int endY = Math.min(nodeY + r, pixelHeight);
		//LOG.debug("Drawing a circle at {},{} spanning from {},{} to {},{}", //
		//		nodeX, nodeY, startX, startY, endX, endY);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				// circle: (x - x_0)^2 + (y - y_0)^2 = r^2
				double height = r * r - ((x - nodeX) * (x - nodeX) + (y - nodeY) * (y - nodeY)); // r^2 - ( (x - x_0)^2 + (y - y_0)^2 )
				if (height >= 0) {
					// max height is r^2, scale down, and scale up to [0, 256) alpha range
					magicColor(x, y, (int)(height / (r * r) * 255), color);
				}
			}
		}
	}

	/**
	 * @param pixels canvas
	 * @param nodeX center x
	 * @param nodeY center y
	 * @param a radius x
	 * @param b radius y
	 * @param color 
	 */
	protected void drawEllipse(int nodeX, int nodeY, int a, int b, int color) {
		int startX = Math.max(nodeX - a, 0);
		int endX = Math.min(nodeX + a, pixelWidth);
		int startY = Math.max(nodeY - b, 0);
		int endY = Math.min(nodeY + b, pixelHeight);
		//LOG.debug("Drawing a circle at {},{} spanning from {},{} to {},{}", //
		//		nodeX, nodeY, startX, startY, endX, endY);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				// ellipse: (x - x_0)^2 / (a^2) + (y - y_0)^2 / (b^2) = 1
				double height = 1 - ((x - nodeX) * (x - nodeX) / (double)(a * a) + (y - nodeY) * (y - nodeY)
						/ (double)(b * b));
				if (height >= 0) {
					magicColor(x, y, (int)(height * 255), color);
				}
			}
		}
	}

	/**
	 * @param pixels canvas
	 * @param x coordinate
	 * @param y coordinate
	 * @param height 0..1
	 * @param color new color to blend without alpha value
	 */
	private void magicColor(int x, int y, int newAlpha, int color) {
		int originalColor = pixels[y * pixelWidth + x];
		if (originalColor == 0) {
			pixels[y * pixelWidth + x] = color | (newAlpha << 24);
		} else {
			int a = Math.max(Color.alpha(originalColor), newAlpha);
			//int a = Math.min(Color.alpha(originalColor) + newAlpha, 255); // stronger lines
			int r = blend(newAlpha, Color.red(originalColor), Color.red(color));
			int b = blend(newAlpha, Color.blue(originalColor), Color.blue(color));
			int g = blend(newAlpha, Color.green(originalColor), Color.green(color));
			pixels[y * pixelWidth + x] = Color.argb(a, r, g, b);
		}
	}

	public static int blend(int newAlpha, int bottom, int top) {
		// return (int)(newAlpha/255.0 * top + (1 - newAlpha/255.0) * bottom);
		return (newAlpha * top + (255 - newAlpha) * bottom) / 255;
	}

	private void border(int borderSize, int borderColor) {
		for (int x = 0; x < borderSize; ++x) {
			for (int y = 0; y < pixelHeight; ++y) {
				pixels[y * pixelWidth + x] = pixels[y * pixelWidth + (pixelWidth - x - 1)] = borderColor;
			}
		}
		for (int y = 0; y < borderSize; ++y) {
			for (int x = 0; x < pixelWidth; ++x) {
				pixels[y * pixelWidth + x] = pixels[(pixelHeight - y - 1) * pixelWidth + x] = borderColor;
			}
		}
	}
}
