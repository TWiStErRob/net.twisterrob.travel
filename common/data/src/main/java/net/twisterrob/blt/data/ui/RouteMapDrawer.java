package net.twisterrob.blt.data.ui;

import java.awt.*;
import java.util.List;
import java.util.*;

import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.Line;
import net.twisterrob.java.exceptions.WTF;
import net.twisterrob.java.model.*;

class RouteMapDrawer extends RouteComponent {
	private static final long serialVersionUID = -5789033664415561910L;

	private Set<StopPoint> stations;

	private Color stopColor;
	private Color stopHighlight;

	private LocationToScreenTransformer transformer;

	private static final Stroke lineStroke = new BasicStroke(4);
	private static final int stopRadius = 10;

	public RouteMapDrawer(Set<StopPoint> stations, Line line, Route route, List<String> highlights) {
		super(line, route, highlights);
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

	@Override public void setLine(Line line) {
		super.setLine(line);
		stopColor = lineColor.darker();
		stopHighlight = new Color(~lineColor.getRGB());
		repaint();
	}

	@Override public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		int top = getInsets().top + stopRadius / 2;
		int left = getInsets().left + stopRadius / 2;
		int width = getWidth() - getInsets().left - getInsets().right - stopRadius;
		int height = getHeight() - getInsets().top - getInsets().bottom - stopRadius;
		transformer.init(width, height);

		Set<StopPoint> drawn = new TreeSet<>(StopPoint.BY_ID);
		if (getRoute() != null) {
			for (RouteSection section : getRoute().getSections()) {
				StopPoint last = null;
				for (RouteLink link : section.getLinks()) {
					paintLink(g2, link, left, top);
					paintStop(g2, link.getFrom(), left, top, last == null? StopType.START : StopType.ROUTE);
					drawn.add(link.getFrom());
					last = link.getTo();
				}
				paintStop(g2, last, left, top, StopType.END);
				drawn.add(last);
			}
		}

		for (StopPoint stopPoint : stations) {
			if (drawn.contains(stopPoint)) {
				continue;
			}
			paintStop(g2, stopPoint, left, top, StopType.IGNORE);
		}
	}

	/**
	 * @param type formerly {@code interchange} {@code true} -> circle, {@code false} -> circle+cross, {@code null} -> dot
	 */
	protected void paintStop(Graphics2D g, StopPoint station, int offsetX, int offsetY, StopType type) {
		Location stopLoc = station.getLocation();
		int stopX = offsetX + transformer.lon2Screen(stopLoc.getLongitude());
		int stopY = offsetY + transformer.lat2Screen(stopLoc.getLatitude());
		switch (type) {
			case ROUTE:
			case IGNORE:
				if (needsHighlight(station)) {
					g.setColor(stopHighlight);
				} else {
					g.setColor(stopColor);
				}
				g.fillOval(stopX - stopRadius / 2, stopY - stopRadius / 2, stopRadius, stopRadius);
				break;
			case INTERCHANGE:
			case START:
				drawStart(g, stopX, stopY);
				break;
			case END:
				drawEnd(g, stopX, stopY);
				break;
			default:
				throw new WTF();
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
		if (needsHighlight(link)) {
			g.setColor(lineHighlight);
		} else {
			g.setColor(lineColor);
		}
		g.drawLine(fromX, fromY, toX, toY);
	}
}
