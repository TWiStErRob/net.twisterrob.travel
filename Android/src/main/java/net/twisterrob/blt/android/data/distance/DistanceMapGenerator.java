package net.twisterrob.blt.android.data.distance;

import java.util.*;

import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.java.model.*;

import org.slf4j.*;

import android.graphics.*;

public class DistanceMapGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapGenerator.class);

	private static final double TIME_PLATFORM_TO_STREET = 1;
	private static final double SPEED_ON_FOOT = 4.5 /* km/h */;

	private static final boolean SIMPLE_TUBE = true;
	private static final DistanceStrategy tubingStrategy = SIMPLE_TUBE
			? new AverageSpeedTubingStrategy()
			: new SmartTubingStrategy();

	private static final double PIXEL_DENSITY = 1000;

	private Map<Integer, NetworkNode> nodes;
	private NetworkNode startNode;

	private final double minLon;
	private final double maxLon;
	private final double minLat;
	private final double maxLat;
	private final double geoWidth;
	private final double geoHeight;
	private final int pixelWidth;
	private final int pixelHeight;

	public DistanceMapGenerator(Map<Integer, NetworkNode> networkNodes, NetworkNode startNode) {
		this.nodes = networkNodes;
		this.startNode = startNode;
		double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for (NetworkNode node: nodes.values()) {
			double lat = node.getPos().getLatitude();
			double lon = node.getPos().getLongitude();
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
		this.pixelWidth = (int)(geoWidth * PIXEL_DENSITY);
		this.pixelHeight = (int)(geoHeight * PIXEL_DENSITY) * 2;

	}

	public Bitmap generate(int borderWidth) {
		int[] pixels = calcPixels();
		if (0 < borderWidth) {
			border(pixels, borderWidth, Color.BLACK);
		}
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, (pixelHeight - 1) * pixelWidth, -pixelWidth, 0, 0, pixelWidth, pixelHeight);
		return bitmap;
	}

	Set<NetworkLink> finished;
	private int[] calcPixels() {
		LOG.debug("Mapping area w={}, h={} to pixels w={}, h={}", //
				geoWidth, geoHeight, pixelWidth, pixelHeight);
		int[] pixels = new int[pixelHeight * pixelWidth];
		//flag(pixels, pixelWidth, pixelHeight);
		finished = new TreeSet<NetworkLink>();
		draw(pixels, startNode, 35);
		return pixels;
	}

	/**
	 * @param node current tube station
	 * @param remainingMinutes minutes remaining from the possible trips
	 */
	private void draw(int[] pixels, NetworkNode node, double remainingMinutes) {
		if (remainingMinutes < 0) {
			return;
		}
		double remainingWalk = (remainingMinutes - TIME_PLATFORM_TO_STREET) / 60.0 /* to hours */* SPEED_ON_FOOT
				* 1000.0 /* to meters */;
		double phi = Math.toRadians(node.getPos().getLatitude());
		double meters_per_lat_degree = LocationConverter.metersPerDegreeLat(phi);
		double meters_per_lon_degree = LocationConverter.metersPerDegreeLon(phi);
		drawCircle(pixels, node.getPos(), remainingWalk / meters_per_lon_degree, remainingWalk / meters_per_lat_degree);
		for (NetworkLink link: node.out) {
			if (finished.add(link)) {
				double travelWithTube = tubingStrategy.distance(node, link);
				draw(pixels, link.m_target, remainingMinutes - travelWithTube);
			}
		}
	}

	/**
	 * @param pixels canvas
	 * @param pos center of the "circle"
	 * @param widthDegrees width of the circle in geo-degrees (longitude)
	 * @param heightDegrees width of the circle in geo-degrees (latitude)
	 */
	protected void drawCircle(int[] pixels, Location pos, double widthDegrees, double heightDegrees) {
		double nodeXOffset = pos.getLongitude() - minLon;
		double nodeYOffset = pos.getLatitude() - minLat;
		int nodeX = (int)(nodeXOffset / geoWidth * pixelWidth);
		int nodeY = (int)(nodeYOffset / geoHeight * pixelHeight);
		int rX = (int)(widthDegrees / geoWidth * pixelWidth);
		int rY = (int)(heightDegrees / geoHeight * pixelHeight);
		drawEllipse(pixels, nodeX, nodeY, rX, rY);
	}

	/**
	 * @param pixels canvas
	 * @param nodeX center x
	 * @param nodeY center y
	 * @param r radius around center
	 */
	protected void drawCircle(int[] pixels, int nodeX, int nodeY, int r) {
		int startX = Math.max(nodeX - r, 0);
		int endX = Math.min(nodeX + r, pixelWidth);
		int startY = Math.max(nodeY - r, 0);
		int endY = Math.min(nodeY + r, pixelHeight);
		LOG.debug("Drawing a circle at {},{} spanning from {},{} to {},{}", //
				nodeX, nodeY, startX, startY, endX, endY);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				// circle: (x - x_0)^2 + (y - y_0)^2 = r^2
				double height = r * r - ((x - nodeX) * (x - nodeX) + (y - nodeY) * (y - nodeY)); // r^2 - ( (x - x_0)^2 + (y - y_0)^2 )
				if (height >= 0) {
					// max height is r^2, scale down
					magicColor(pixels, x, y, height / (r * r));
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
	 */
	protected void drawEllipse(int[] pixels, int nodeX, int nodeY, int a, int b) {
		int startX = Math.max(nodeX - a, 0);
		int endX = Math.min(nodeX + a, pixelWidth);
		int startY = Math.max(nodeY - b, 0);
		int endY = Math.min(nodeY + b, pixelHeight);
		LOG.debug("Drawing a circle at {},{} spanning from {},{} to {},{}", //
				nodeX, nodeY, startX, startY, endX, endY);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				// ellipse: (x - x_0)^2 / (a^2) + (y - y_0)^2 / (b^2) = 1
				double height = 1 - ((x - nodeX) * (x - nodeX) / (double)(a * a) + (y - nodeY) * (y - nodeY)
						/ (double)(b * b));
				if (height >= 0) {
					magicColor(pixels, x, y, height);
				}
			}
		}
	}

	/**
	 * @param pixels canvas
	 * @param x coordinate
	 * @param y coordinate
	 * @param height 0..1
	 */
	private void magicColor(int[] pixels, int x, int y, double height) {
		int oldAlpha = Color.alpha(pixels[y * pixelWidth + x]);
		int newAlpha = (int)(height * 255);
		pixels[y * pixelWidth + x] = Color.argb(Math.max(oldAlpha, newAlpha), 255, 0, 0);
	}

	private void border(int[] pixels, int borderSize, int borderColor) {
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

	@SuppressWarnings("unused")
	private void flag(int[] pixels) {
		for (int x = 0; x < pixelWidth; ++x) {
			for (int y = 0; y < pixelHeight / 3; ++y) {
				pixels[y * pixelWidth + x] = 0xFFFF0000;
			}
			for (int y = pixelHeight / 3; y < pixelHeight / 3 * 2; ++y) {
				pixels[y * pixelWidth + x] = 0xFFFFFFFF;
			}
			for (int y = pixelHeight / 3 * 2; y < pixelHeight; ++y) {
				pixels[y * pixelWidth + x] = 0xFF00FF00;
			}
		}
	}
}
