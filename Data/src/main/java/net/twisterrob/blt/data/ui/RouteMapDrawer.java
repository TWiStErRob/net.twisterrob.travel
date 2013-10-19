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
	private static final Stroke crossStroke = new BasicStroke(1);
	private static final int stopRadius = 10;
	private static final int outerRadius = 18;
	private static final int innerRadius = 12;

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

		Set<StopPoint> drawn = new TreeSet<>(StopPoint.BY_ID);
		if (route != null) {
			for (RouteSection section: route.getRouteSections()) {
				StopPoint last = null;
				for (RouteLink link: section.getRouteLinks()) {
					paintLink(g2, link, left, top);
					paintStop(g2, link.getFrom(), left, top, last == null? StopType.START : StopType.ROUTE);
					drawn.add(link.getFrom());
					last = link.getTo();
				}
				paintStop(g2, last, left, top, StopType.END);
				drawn.add(last);
			}
		}

		for (StopPoint stopPoint: stations) {
			if (drawn.contains(stopPoint)) {
				continue;
			}
			paintStop(g2, stopPoint, left, top, StopType.IGNORE);
		}
	}

	private static enum StopType {
		START,
		ROUTE,
		INTERCHANGE,
		END,
		IGNORE
	}

	/**
	 * @param interchange <code>true</code> -> circle, <code>false</code> -> circle+cross, <code>null</code> -> dot
	 */
	protected void paintStop(Graphics2D g, StopPoint station, int offsetX, int offsetY, StopType type) {
		Location stopLoc = station.getLocation();
		int stopX = offsetX + transformer.lon2Screen(stopLoc.getLongitude());
		int stopY = offsetY + transformer.lat2Screen(stopLoc.getLatitude());
		switch (type) {
			case ROUTE:
			case IGNORE:
				if (highlights.contains(station.getName())) {
					g.setColor(stopHighlight);
				} else {
					g.setColor(stopColor);
				}
				g.fillOval(stopX - stopRadius / 2, stopY - stopRadius / 2, stopRadius, stopRadius);
				break;
			case START:
			case END:
				g.setColor(Color.BLACK);
				g.fillOval(stopX - outerRadius / 2, stopY - outerRadius / 2, outerRadius, outerRadius);
				g.setColor(Color.WHITE);
				g.fillOval(stopX - innerRadius / 2, stopY - innerRadius / 2, innerRadius, innerRadius);
				if (type == StopType.END) {
					g.setStroke(crossStroke);
					g.setColor(Color.BLACK);
					g.drawLine(stopX - outerRadius / 2, stopY - outerRadius / 2, stopX + outerRadius / 2, stopY
							+ outerRadius / 2);
				}
		}
	}

	protected void paintLink(Graphics2D g, RouteLink link, int offsetX, int offsetY) {
		StopPoint from = link.getFrom();
		Location fromLoc = from.getLocation();
		int fromX = offsetX + transformer.lon2Screen(fromLoc.getLongitude());
		int fromY = offsetY + transformer.lat2Screen(fromLoc.getLatitude());

		StopPoint to = link.getTo();
		Location toLoc = to.getLocation();
		int toX = offsetX + transformer.lon2Screen(toLoc.getLongitude());
		int toY = offsetY + transformer.lat2Screen(toLoc.getLatitude());

		g.setStroke(lineStroke);
		if (highlights.contains(from.getName()) && highlights.contains(to.getName())) {
			g.setColor(lineHighlight);
		} else {
			g.setColor(lineColor);
		}
		g.drawLine(fromX, fromY, toX, toY);
	}

}