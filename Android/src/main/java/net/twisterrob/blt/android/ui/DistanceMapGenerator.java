package net.twisterrob.blt.android.ui;

import java.util.*;

import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.java.model.Location;

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
	private final int pixelWidth;
	private final int pixelHeight;

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
		// *2 makes the pixels square, because the geo coordinate system is twice as wide as tall
		this.pixelWidth = (int)(geoWidth * DENSITY);
		this.pixelHeight = (int)(geoHeight * DENSITY) * 2;

	}

	public Bitmap generate(int borderWidth) {
		int[] pixels = calcPixels();
		if (0 < borderWidth) {
			border(pixels, borderWidth);
		}
		Bitmap bitmap = Bitmap.createBitmap(pixelWidth, pixelHeight, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, (pixelHeight - 1) * pixelWidth, -pixelWidth, 0, 0, pixelWidth, pixelHeight);
		return bitmap;
	}

	Set<Integer> finished;
	private int[] calcPixels() {
		LOG.debug("Mapping area w={}, h={} to pixels w={}, h={}", //
				geoWidth, geoHeight, pixelWidth, pixelHeight);
		int[] pixels = new int[pixelHeight * pixelWidth];
		//flag(pixels, pixelWidth, pixelHeight);
		finished = new TreeSet<Integer>();
		draw(pixels, startNode, pixelWidth / 7);
		return pixels;
	}

	private void draw(int[] pixels, NetworkNode node, int remaining) {
		finished.add(node.getID());
		drawCircle(pixels, node.getPos(), remaining, remaining / 3);
		for (NetworkLink link: node.out) {
			if (!finished.contains(link.m_target.getID())) {
				draw(pixels, link.m_target, (int)(remaining * 0.6));
			}
		}
	}

	protected void drawCircle(int[] pixels, Location pos, int rX, int rY) {
		double nodeXOffset = pos.getLongitude() - minLon;
		double nodeYOffset = pos.getLatitude() - minLat;
		int nodeX = (int)(nodeXOffset / geoWidth * pixelWidth);
		int nodeY = (int)(nodeYOffset / geoHeight * pixelHeight);
		drawEllipse(pixels, nodeX, nodeY, rX, rY);
	}

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

	protected void drawEllipse(int[] pixels, int nodeX, int nodeY, int rX, int rY) {
		int startX = Math.max(nodeX - rX, 0);
		int endX = Math.min(nodeX + rX, pixelWidth);
		int startY = Math.max(nodeY - rY, 0);
		int endY = Math.min(nodeY + rY, pixelHeight);
		LOG.debug("Drawing a circle at {},{} spanning from {},{} to {},{}", //
				nodeX, nodeY, startX, startY, endX, endY);
		for (int x = startX; x < endX; ++x) {
			for (int y = startY; y < endY; ++y) {
				// ellipse: (x - x_0)^2 / (a^2) + (y - y_0)^2 / (b^2) = 1
				double height = 1 - ((x - nodeX) * (x - nodeX) / (double)(rX * rX) + (y - nodeY) * (y - nodeY)
						/ (double)(rY * rY));
				if (height >= 0) {
					magicColor(pixels, x, y, height);
				}
			}
		}
	}

	/**
	 * @param pixels
	 * @param x
	 * @param y
	 * @param height 0..1
	 */
	private void magicColor(int[] pixels, int x, int y, double height) {
		int oldAlpha = Color.alpha(pixels[y * pixelWidth + x]);
		int newAlpha = (int)(height * height * 255); // square for more attenuation
		pixels[y * pixelWidth + x] = Color.argb(Math.max(oldAlpha, newAlpha), 255, 0, 0);
	}

	private void border(int[] pixels, int border) {
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
