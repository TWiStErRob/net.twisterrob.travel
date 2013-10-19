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
	private static final int stopHeight = 10;
	private static final int textDistance = 10;

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
		List<StopPoint> stops = route.getStopPoints();
		int stopWidth = getWidth() / stops.size();
		int midHeight = getHeight() / 2;
		g.setColor(stopColor);
		g.fillRect(stopWidth / 4, midHeight - lineThick / 2, getWidth() - stopWidth / 2, lineThick);

		for (int i = 0; i < stops.size(); ++i) {
			StopPoint stop = stops.get(i);
			int left = stopWidth * i;
			int right = stopWidth * (i + 1);
			int midPos = (left + right) / 2;
			if (highlights.contains(stop.getName())) {
				g.setColor(stopHighlight);
			} else {
				g.setColor(stopColor);
			}
			if (i % 2 == 0) {
				g.fillRect(midPos - stopThick / 2, midHeight - stopHeight, stopThick, stopHeight); // above
			} else {
				g.fillRect(midPos - stopThick / 2, midHeight - 0, stopThick, stopHeight); // below
			}

			String name = stop.getName();
			int x = midPos - g.getFontMetrics().stringWidth(name) / 2;
			int y;
			if (i % 2 == 0) {
				y = midHeight - (stopHeight + textDistance) + 0 /* y == baseline */; // above
			} else {
				y = midHeight + (stopHeight + textDistance) + g.getFontMetrics().getAscent(); // below
			}
			if (highlights.contains(stop.getName())) {
				g.setColor(textHighlight);
			} else {
				g.setColor(textColor);
			}
			g.drawString(name, x, y);
		}
	}
}