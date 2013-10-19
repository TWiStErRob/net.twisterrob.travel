package net.twisterrob.blt.data.ui;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JPanel;

import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.*;

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

	private LocationToScreenTransformer transformer;

	private static final Stroke lineStroke = new BasicStroke(4);
	private static final int stopRadius = 10;

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
		transformer = new LocationToScreenTransformer(StopPoint.getLocations(stations));
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

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		int top = getInsets().top + stopRadius / 2;
		int left = getInsets().left + stopRadius / 2;
		int width = getWidth() - getInsets().left - getInsets().right - stopRadius;
		int height = getHeight() - getInsets().top - getInsets().bottom - stopRadius;
		transformer.init(width, height);

		for (StopPoint station: stations) {
			Location stopLoc = station.getLocation();
			int stopX = left + transformer.lon2Screen(stopLoc.getLongitude());
			int stopY = top + transformer.lat2Screen(stopLoc.getLatitude());
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
				int fromX = left + transformer.lon2Screen(fromLoc.getLongitude());
				int fromY = top + transformer.lat2Screen(fromLoc.getLatitude());

				StopPoint to = link.getTo();
				Location toLoc = to.getLocation();
				int toX = left + transformer.lon2Screen(toLoc.getLongitude());
				int toY = top + transformer.lat2Screen(toLoc.getLatitude());

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