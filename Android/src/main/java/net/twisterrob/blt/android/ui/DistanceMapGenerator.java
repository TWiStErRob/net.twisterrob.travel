package net.twisterrob.blt.android.ui;

import java.util.Map;

import net.twisterrob.blt.android.db.model.NetworkNode;

import org.slf4j.*;

import android.graphics.*;

public class DistanceMapGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapGenerator.class);

	private static final double DENSITY = 1000;
	private Map<Integer, NetworkNode> nodes;
	private NetworkNode startNode;

	private final double minLon;
	private final double maxLon;
	private final double minLat;
	private final double maxLat;
	private final double geoWidth;
	private final double geoHeight;

	public DistanceMapGenerator(Map<Integer, NetworkNode> nodes, NetworkNode startNode) {
		this.nodes = nodes;
		this.startNode = startNode;
		double minLon = Double.POSITIVE_INFINITY, maxLon = Double.NEGATIVE_INFINITY;
		double minLat = Double.POSITIVE_INFINITY, maxLat = Double.NEGATIVE_INFINITY;
		for (NetworkNode node: nodes.values()) {
			double lat = node.getPos().getLatitude();
			double lon = node.getPos().getLongitude();
			if (lon < minLon) {
				minLon = lon;
			}
			if (maxLon < lon) {
				maxLon = lon;
			}
			if (lat < minLat) {
				minLat = lat;
			}
			if (maxLat < lat) {
				maxLat = lat;
			}
		}
		this.minLon = minLon;
		this.maxLon = maxLon;
		this.minLat = minLat;
		this.maxLat = maxLat;
		this.geoWidth = maxLon - minLon;
		this.geoHeight = maxLat - minLat;
	}

	public Bitmap generate(int borderWidth) {
		// *2 makes the pixels square, because the geo coordinate system is twice as wide as tall
		int pixelWidth = (int)((maxLon - minLon) * DENSITY);
		int pixelHeight = (int)((maxLat - minLat) * DENSITY) * 2;
		int[] pixels = calcPixels(pixelWidth, pixelHeight);
		if (0 < borderWidth) {
			border(pixels, pixelWidth, pixelHeight, borderWidth);
		}
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, (pixelHeight - 1) * pixelWidth, -pixelWidth, 0, 0, pixelWidth, pixelHeight);
		return bitmap;
	}

	private int[] calcPixels(int pixelWidth, int pixelHeight) {
		LOG.debug("Mapping area w={}, h={} to pixels w={}, h={}", //
				geoWidth, geoHeight, pixelWidth, pixelHeight);
		int[] pixels = new int[pixelHeight * pixelWidth];
		double nodeXOffset = startNode.getPos().getLongitude() - minLon;
		double nodeYOffset = startNode.getPos().getLatitude() - minLat;
		int nodeX = (int)(nodeXOffset / geoWidth * pixelWidth);
		int nodeY = (int)(nodeYOffset / geoHeight * pixelHeight);
		int r = pixelWidth / 7;
		int startX = Math.max(nodeX - r, 0);
		int endX = Math.min(nodeX + r, pixelWidth);
		int startY = Math.max(nodeY - r, 0);
		int endY = Math.min(nodeY + r, pixelHeight);
		//flag(pixels, pixelWidth, pixelHeight);
		LOG.debug("Drawing a circle at {},{} spanning from {},{} to {},{}", //
				nodeX, nodeY, startX, startY, endX, endY);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				// r^2 - ( (x - x_0)^2 + (y - y_0)^2 )
				double height = r * r - ((x - nodeX) * (x - nodeX) + (y - nodeY) * (y - nodeY));
				if (height >= 0) {
					// max height is r^2, scale down, and then up to [0, 256) range
					int grey = (int)(height / (r * r) * 0xff);
					pixels[y * pixelWidth + x] = Color.argb(grey, 255, 0, 0);
					//LOG.trace("{},{} = {}/{} ({})", //
					//x, y, height, grey, Integer.toHexString(pixels[y * pixelWidth + x]));
				}
			}
		}
		return pixels;
	}

	private static void border(int[] pixels, int pixelWidth, int pixelHeight, int border) {
		for (int x = 0; x < border; ++x) {
			for (int y = 0; y < pixelHeight; ++y) {
				pixels[y * pixelWidth + x] = pixels[y * pixelWidth + (pixelWidth - x - 1)] = 0xFF000000;
			}
		}
		for (int y = 0; y < border; ++y) {
			for (int x = 0; x < pixelWidth; ++x) {
				pixels[y * pixelWidth + x] = pixels[(pixelHeight - y - 1) * pixelWidth + x] = 0xFF000000;
			}
		}
	}

	private static void flag(int[] pixels, int pixelWidth, int pixelHeight) {
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
