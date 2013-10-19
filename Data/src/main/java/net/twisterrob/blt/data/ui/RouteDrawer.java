package net.twisterrob.blt.data.ui;

import java.awt.*;
import java.util.List;

import javax.swing.JPanel;

import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.*;

class RouteDrawer extends JPanel {
	private static final long serialVersionUID = 1L;
	private Line line;
	private Route route;
	private List<String> highlights;

	private LineColors colors = new TubeStatusPresentationLineColors();
	private Color stopColor;
	private Color stopHighlight;
	private static final Color textColor = Color.BLACK;
	private static final Color textHighlight = Color.RED;

	private static final int lineThick = 6;
	private static final int stopThick = 6;
	private static final int outerRadius = 18;
	private static final int innerRadius = 12;
	private static final int stopHeight = 10;
	private static final int textDistance = 10;
	private static final int internalPadding = 25;

	public RouteDrawer(Line line, Route route, List<String> highlights) {
		setLine(line);
		setRoute(route);
		setHighlights(highlights);
		setPreferredSize(new Dimension(-1, 100));
		setBackground(Color.WHITE);
	}

	public Line getLine() {
		return line;
	}
	public void setLine(Line line) {
		this.line = line;
		stopColor = new Color(line.getBackground(colors));
		stopHighlight = new Color(~stopColor.getRGB());
		repaint();
	}
	public List<String> getHighlights() {
		return highlights;
	}
	public void setHighlights(List<String> highlights) {
		this.highlights = highlights;
		repaint();
	}

	public Route getRoute() {
		return route;
	}
	public void setRoute(Route route) {
		this.route = route;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (route == null) {
			return;
		}
		int top = getInsets().top;
		int left = getInsets().left + internalPadding;
		int width = getWidth() - getInsets().left - getInsets().right - internalPadding * 2;
		int height = getHeight() - getInsets().top - getInsets().bottom;

		List<RouteLink> links = route.getRouteLinks();
		int linkWidth = width / links.size();
		left += (width - linkWidth * links.size()) / 2; // center align based on rounding error
		int midHeight = top + height / 2;

		StopPoint last = null;
		int stopLeft = left;
		boolean above = false;
		for (int i = 0; i < links.size(); ++i) {
			RouteLink link = links.get(i);
			if (highlights.contains(link.getFrom().getName()) && highlights.contains(link.getTo().getName())) {
				g.setColor(stopHighlight);
			} else {
				g.setColor(stopColor);
			}
			g.fillRect(stopLeft, midHeight - lineThick / 2, linkWidth, lineThick);

			StopPoint stop = link.getFrom();
			paintStop(g, stop, above, stopLeft, midHeight, i == 0? StopType.START : StopType.ROUTE);

			// prepare next
			last = stop;
			above = !above;
			stopLeft += linkWidth;
		}
		paintStop(g, last, links.size() % 2 == 0, stopLeft, midHeight, StopType.END);
	}

	private static enum StopType {
		START,
		ROUTE,
		INTERCHANGE,
		END,
		IGNORE
	}

	protected void paintStop(Graphics g, StopPoint stop, boolean indicateAbove, int stopX, int stopY, StopType type) {
		// draw stop indicator
		switch (type) {
			case ROUTE:
			case IGNORE:
				if (highlights.contains(stop.getName())) {
					g.setColor(stopHighlight);
				} else {
					g.setColor(stopColor);
				}
				if (indicateAbove) { // alternate above/below
					g.fillRect(stopX - stopThick / 2, stopY - stopHeight, stopThick, stopHeight); // above
				} else {
					g.fillRect(stopX - stopThick / 2, stopY - 0, stopThick, stopHeight); // below
				}
				break;
			case START:
			case END:
				g.setColor(Color.BLACK);
				g.fillOval(stopX - outerRadius / 2, stopY - outerRadius / 2, outerRadius, outerRadius);
				g.setColor(Color.WHITE);
				g.fillOval(stopX - innerRadius / 2, stopY - innerRadius / 2, innerRadius, innerRadius);
				if (type == StopType.END) {
					g.setColor(Color.BLACK);
					g.drawLine(stopX - outerRadius / 2, stopY - outerRadius / 2, stopX + outerRadius / 2, stopY
							+ outerRadius / 2);
				}
		}

		String name = stop.getName();
		// draw stop name
		int textX = stopX - g.getFontMetrics().stringWidth(name) / 2;
		int textY;
		if (indicateAbove) { // alternate above/below
			textY = stopY - (stopHeight + textDistance) + 0 /* y == baseline */; // above
		} else {
			textY = stopY + (stopHeight + textDistance) + g.getFontMetrics().getAscent()
					- g.getFontMetrics().getDescent(); // below
		}
		if (highlights.contains(stop.getName())) {
			g.setColor(textHighlight);
		} else {
			g.setColor(textColor);
		}
		g.drawString(name, textX, textY);
	}
}