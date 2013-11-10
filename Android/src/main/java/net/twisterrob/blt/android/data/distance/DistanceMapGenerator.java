package net.twisterrob.blt.android.data.distance;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.model.Line;
import net.twisterrob.java.model.*;

import org.slf4j.*;

import android.graphics.*;

@NotThreadSafe
public class DistanceMapGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapGenerator.class);

	private Map<Integer, NetworkNode> nodes;
	private NetworkLink startLink;
	private Set<NetworkLink> finishedNodes;

	private final DistanceMapConfig config;

	private final double minLon;
	private final double maxLon;
	private final double minLat;
	private final double maxLat;
	private final double geoWidth;
	private final double geoHeight;

	private final int pixelWidth;
	private final int pixelHeight;
	private int[] pixels;

	public DistanceMapGenerator(Map<Integer, NetworkNode> networkNodes, NetworkLink startLink, DistanceMapConfig config) {
		this.nodes = networkNodes;
		this.startLink = startLink;
		this.config = config;
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
		this.pixelWidth = (int)(geoWidth * config.pixelDensity);
		this.pixelHeight = (int)(geoHeight * config.pixelDensity) * 2;
	}

	public DistanceMapConfig getConfig() {
		return config;
	}

	public Bitmap generate(double minutes) {
		calcPixels(minutes);
		if (0 < config.borderSize) {
			border(config.borderSize, config.borderColor);
		}
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, (pixelHeight - 1) * pixelWidth, -pixelWidth, 0, 0, pixelWidth, pixelHeight);
		pixels = null;
		return bitmap;
	}

	private int[] calcPixels(double minutes) {
		LOG.debug("Mapping area w={}, h={} to pixels w={}, h={}", //
				geoWidth, geoHeight, pixelWidth, pixelHeight);
		pixels = new int[pixelHeight * pixelWidth];
		//flag(pixels, pixelWidth, pixelHeight);
		finishedNodes = new TreeSet<NetworkLink>();
		draw(startLink, minutes);
		return pixels;
	}

	/**
	 * @param node current tube station
	 * @param remainingMinutes minutes remaining from the possible trips
	 */
	private void draw(NetworkLink startLink, double remainingMinutes) {
		if (remainingMinutes < 0) {
			return;
		}
		NetworkNode node = startLink.m_target;
		double remainingWalk = (remainingMinutes - config.timePlatformToStreet) / 60.0 /* to hours */
				* config.speedOnFoot * 1000.0 /* to meters */;
		double phi = Math.toRadians(node.getPos().getLatitude());
		double meters_per_lat_degree = LocationConverter.metersPerDegreeLat(phi);
		double meters_per_lon_degree = LocationConverter.metersPerDegreeLon(phi);
		drawCircle(node.getPos(), startLink.getLine(), remainingWalk / meters_per_lon_degree, remainingWalk
				/ meters_per_lat_degree);

		for (NetworkLink link: priorityOuts(startLink)) {
			if (finishedNodes.add(link)) {
				double travelWithTube = config.tubingStrategy.distance(node, link);
				draw(link, remainingMinutes - travelWithTube);
			}
		}
	}

	private Iterable<NetworkLink> priorityOuts(final NetworkLink startLink) {
		TreeSet<NetworkLink> newOuts = new TreeSet<NetworkLink>(new Comparator<NetworkLink>() {
			public int compare(NetworkLink lhs, NetworkLink rhs) {
				int line = lhs.getLine().compareTo(rhs.getLine());
				int target = lhs.m_target.compareTo(rhs.m_target);
				boolean lhsSame = lhs.getLine() == startLink.getLine();
				boolean rhsSame = rhs.getLine() == startLink.getLine();
				if (lhsSame && line != 0) {
					return -1;
				} else if (rhsSame && line != 0) {
					return 1;
				} else if (rhsSame && line == 0) {
					return target;
				} else if (lhsSame && line == 0) {
					return target;
				} else if (line == 0) {
					return target;
				} else {
					return line;
				}
			}
		});
		newOuts.addAll(startLink.getTarget().out);
		return newOuts;
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
		LOG.debug("Drawing a circle at {},{} spanning from {},{} to {},{}", //
				nodeX, nodeY, startX, startY, endX, endY);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				// circle: (x - x_0)^2 + (y - y_0)^2 = r^2
				double height = r * r - ((x - nodeX) * (x - nodeX) + (y - nodeY) * (y - nodeY)); // r^2 - ( (x - x_0)^2 + (y - y_0)^2 )
				if (height >= 0) {
					// max height is r^2, scale down
					magicColor(x, y, height / (r * r), color);
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
		LOG.debug("Drawing a circle at {},{} spanning from {},{} to {},{}", //
				nodeX, nodeY, startX, startY, endX, endY);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				// ellipse: (x - x_0)^2 / (a^2) + (y - y_0)^2 / (b^2) = 1
				double height = 1 - ((x - nodeX) * (x - nodeX) / (double)(a * a) + (y - nodeY) * (y - nodeY)
						/ (double)(b * b));
				if (height >= 0) {
					magicColor(x, y, height, color);
				}
			}
		}
	}

	/**
	 * @param pixels canvas
	 * @param x coordinate
	 * @param y coordinate
	 * @param height 0..1
	 * @param color 
	 */
	private void magicColor(int x, int y, double height, int color) {
		int oldAlpha = Color.alpha(pixels[y * pixelWidth + x]);
		int newAlpha = (int)(height * 255);
		if (newAlpha > oldAlpha) {
			pixels[y * pixelWidth + x] = color | (newAlpha << 24);
		}
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

	@SuppressWarnings("unused")
	private void flag() {
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
