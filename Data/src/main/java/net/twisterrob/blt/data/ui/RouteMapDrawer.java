package net.twisterrob.blt.data.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JPanel;

import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.Location;

class RouteMapDrawer extends JPanel {
	private static final long serialVersionUID = 1L;

	private Line line;
	private Set<StopPoint> stations;
	private Route route;
	private List<String> highlights;

	private @Nonnull LineColors colors = new TubeStatusPresentationLineColors();
	private Color lineColor;
	private Color lineHighlight;
	private Color stopColor;
	private Color stopHighlight;

	private static final Stroke lineStroke = new BasicStroke(4);
	private static final int stopRadius = 10;

	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;

	public RouteMapDrawer(Set<StopPoint> stations, Line line, Route route, List<String> highlights) {
		setLine(line);
		setRoute(route);
		setHighlights(highlights);
		setStations(stations);
	}

	public Set<StopPoint> getStations() {
		return stations;
	}
	public void setStations(Set<StopPoint> stations) {
		this.stations = stations;
		minMax();
		repaint();
	}

	public Line getLine() {
		return line;
	}
	public void setLine(Line line) {
		this.line = line;
		lineColor = new Color(line.getBackground(colors));
		lineHighlight = new Color(~lineColor.getRGB());
		stopColor = lineColor.darker();
		stopHighlight = new Color(~lineColor.getRGB());
		repaint();
	}
	public Route getRoute() {
		return route;
	}
	public void setRoute(Route route) {
		this.route = route;
		repaint();
	}
	public List<String> getHighlights() {
		return highlights;
	}
	public void setHighlights(List<String> highlights) {
		this.highlights = highlights;
		repaint();
	}

	private void minMax() {
		minLat = Double.POSITIVE_INFINITY;
		maxLat = Double.NEGATIVE_INFINITY;
		minLon = Double.POSITIVE_INFINITY;
		maxLon = Double.NEGATIVE_INFINITY;
		for (StopPoint station: stations) {
			Location loc = station.getLocation();
			double lat = loc.getLatitude();
			double lon = loc.getLongitude();
			if (lat < minLat) {
				minLat = lat;
			}
			if (lat > maxLat) {
				maxLat = lat;
			}
			if (lon < minLon) {
				minLon = lon;
			}
			if (lon > maxLon) {
				maxLon = lon;
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		int top = getInsets().top + stopRadius / 2;
		int left = getInsets().left + stopRadius / 2;
		int width = getWidth() - getInsets().left - getInsets().right - stopRadius;
		int height = getHeight() - getInsets().top - getInsets().bottom - stopRadius;

		double geoOffX = minLon, geoOffY = minLat;
		double geoScaleX = width / (maxLon - minLon);
		double geoScaleY = height / (maxLat - minLat);
		geoScaleX = Math.min(geoScaleX, geoScaleY); // preserve aspect ratio
		geoScaleY = /* reverse vertically */-1 * Math.min(geoScaleX, geoScaleY); // preserve aspect ratio
		int geoAlignX = (width - (int)((maxLon - geoOffX) * geoScaleX)) / 2; // center horizontally
		int geoAlignY = (height - (int)((maxLat - geoOffY) * geoScaleY)) / 2; // center vertically
		for (StopPoint station: stations) {
			Location stopLoc = station.getLocation();
			int stopX = left + geoAlignX + (int)((stopLoc.getLongitude() - geoOffX) * geoScaleX);
			int stopY = top + geoAlignY + (int)((stopLoc.getLatitude() - geoOffY) * geoScaleY);
			if (highlights.contains(station.getName())) {
				g.setColor(stopHighlight);
			} else {
				g.setColor(stopColor);
			}
			g.fillOval(stopX - stopRadius / 2, stopY - stopRadius / 2, stopRadius, stopRadius);
		}
		if (route == null) {
			return;
		}
		g2.setStroke(lineStroke);
		for (RouteSection section: route.getRouteSections()) {
			for (RouteLink link: section.getRouteLinks()) {
				StopPoint from = link.getFrom();
				Location fromLoc = from.getLocation();
				int fromX = left + (int)((fromLoc.getLongitude() - geoOffX) * geoScaleX) + geoAlignX;
				int fromY = top + (int)((fromLoc.getLatitude() - geoOffY) * geoScaleY) + geoAlignY;

				StopPoint to = link.getTo();
				Location toLoc = to.getLocation();
				int toX = left + (int)((toLoc.getLongitude() - geoOffX) * geoScaleX) + geoAlignX;
				int toY = top + (int)((toLoc.getLatitude() - geoOffY) * geoScaleY) + geoAlignY;

				if (highlights.contains(from.getName()) && highlights.contains(to.getName())) {
					g.setColor(lineHighlight);
				} else {
					g.setColor(lineColor);
				}
				g.drawLine(fromX, fromY, toX, toY);
			}
		}
	}
}