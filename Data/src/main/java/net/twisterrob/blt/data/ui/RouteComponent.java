package net.twisterrob.blt.data.ui;

import java.awt.*;
import java.util.List;

import javax.swing.JPanel;

import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.*;

public abstract class RouteComponent extends JPanel {
	private static final long serialVersionUID = -846538337162491330L;

	private Line line;
	private Route route;
	private List<String> highlights;

	private LineColors colors = new TubeStatusPresentationLineColors();
	protected Color lineColor;
	protected Color lineHighlight;

	private static final Stroke crossStroke = new BasicStroke(2);
	private static final int outerRadius = 18;
	private static final int innerRadius = 12;

	public RouteComponent(Line line, Route route, List<String> highlights) {
		setLine(line);
		setRoute(route);
		setHighlights(highlights);
	}

	public Line getLine() {
		return line;
	}
	public void setLine(Line line) {
		this.line = line;
		lineColor = new Color(line.getBackground(colors));
		lineHighlight = new Color(~lineColor.getRGB());
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

	protected boolean needsHighlight(RouteLink link) {
		return highlights.contains(link.getFrom().getName()) && highlights.contains(link.getTo().getName());
	}
	protected boolean needsHighlight(StopPoint stop) {
		return highlights.contains(stop.getName());
	}

	@SuppressWarnings("static-method")
	protected void drawStart(Graphics2D g, int stopX, int stopY) {
		g.setColor(Color.BLACK);
		g.fillOval(stopX - outerRadius / 2, stopY - outerRadius / 2, outerRadius, outerRadius);
		g.setColor(Color.WHITE);
		g.fillOval(stopX - innerRadius / 2, stopY - innerRadius / 2, innerRadius, innerRadius);
	}

	protected void drawEnd(Graphics2D g, int stopX, int stopY) {
		drawStart(g, stopX, stopY);
		g.setStroke(crossStroke);
		g.setColor(Color.BLACK);
		g.drawLine(stopX - outerRadius / 2, stopY - outerRadius / 2, stopX + outerRadius / 2, stopY + outerRadius / 2);
	}

	protected static enum StopType {
		START,
		ROUTE,
		INTERCHANGE,
		END,
		IGNORE
	}
}
