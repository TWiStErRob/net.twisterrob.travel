package net.twisterrob.blt.data.ui;

import java.awt.*;
import java.util.List;

import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.Line;
import net.twisterrob.java.exceptions.WTF;

class RouteDrawer extends RouteComponent {
	private static final long serialVersionUID = -1205461348327571143L;

	private static final Color textColor = Color.BLACK;
	private static final Color textHighlight = Color.RED;

	private static final int lineThick = 6;
	private static final int stopThick = 6;
	private static final int stopHeight = 10;
	private static final int textDistance = 10;
	private static final int internalPadding = 25;

	public RouteDrawer(Line line, Route route, List<String> highlights) {
		super(line, route, highlights);
		setPreferredSize(new Dimension(-1, 100));
		setBackground(Color.WHITE);
	}
	@Override public void paint(Graphics g) {
		super.paint(g);
		if (getRoute() == null) {
			return;
		}
		Graphics2D g2 = (Graphics2D)g;
		int top = getInsets().top;
		int left = getInsets().left + internalPadding;
		int width = getWidth() - getInsets().left - getInsets().right - internalPadding * 2;
		int height = getHeight() - getInsets().top - getInsets().bottom;

		List<RouteLink> links = getRoute().getLinks();
		int linkWidth = width / links.size();
		left += (width - linkWidth * links.size()) / 2; // center align based on rounding error
		int midHeight = top + height / 2;

		StopPoint last = null;
		int stopLeft = left;
		boolean above = false;
		for (int i = 0; i < links.size(); ++i) {
			RouteLink link = links.get(i);
			if (needsHighlight(link)) {
				g.setColor(lineHighlight);
			} else {
				g.setColor(lineColor);
			}
			g.fillRect(stopLeft, midHeight - lineThick / 2, linkWidth, lineThick);

			StopPoint stop = link.getFrom();
			paintStop(g2, stop, above, stopLeft, midHeight, i == 0? StopType.START : StopType.ROUTE);

			// prepare next
			last = link.getTo();
			above = !above;
			stopLeft += linkWidth;
		}
		paintStop(g2, last, above, stopLeft, midHeight, StopType.END);
	}

	protected void paintStop(Graphics2D g, StopPoint stop, boolean indicateAbove, int stopX, int stopY, StopType type) {
		// draw stop indicator
		switch (type) {
			case ROUTE:
			case IGNORE:
				if (needsHighlight(stop)) {
					g.setColor(lineHighlight);
				} else {
					g.setColor(lineColor);
				}
				if (indicateAbove) { // alternate above/below
					g.fillRect(stopX - stopThick / 2, stopY - stopHeight, stopThick, stopHeight); // above
				} else {
					g.fillRect(stopX - stopThick / 2, stopY - 0, stopThick, stopHeight); // below
				}
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

		String name = cleanStopName(stop.getName());
		// draw stop name
		int textX = stopX - g.getFontMetrics().stringWidth(name) / 2;
		int textY;
		if (indicateAbove) { // alternate above/below
			textY = stopY - (stopHeight + textDistance) + 0 /* y == baseline */; // above
		} else {
			textY = stopY + (stopHeight + textDistance) + g.getFontMetrics().getAscent()
					- g.getFontMetrics().getDescent(); // below
		}
		if (needsHighlight(stop)) {
			g.setColor(textHighlight);
		} else {
			g.setColor(textColor);
		}
		g.drawString(name, textX, textY);
	}
}
