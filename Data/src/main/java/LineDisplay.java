import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.*;

import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.Route;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.RouteLink;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.RouteSection;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.StopPoint;
import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.Location;

public class LineDisplay extends JFrame {
	private static final long serialVersionUID = 1L;
	protected JList list;
	protected RouteMapDrawer routeMap;
	protected RouteDrawer routeLine;
	protected Color fg;
	protected Color bg;

	public LineDisplay(final @Nonnull Line line, @Nonnull List<Route> routes) {
		super(line.getTitle());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1024, 800));

		LineColors colors = new TubeStatusPresentationLineColors();
		fg = new Color(line.getForeground(colors));
		bg = new Color(line.getBackground(colors));

		Set<StopPoint> stopPoints = JourneyPlannerTimetableFeed.getStopPoints(routes);

		Container panel = getContentPane();
		panel.setLayout(new BorderLayout());

		routeLine = new RouteDrawer();
		panel.add(routeLine, BorderLayout.SOUTH);

		list = new JList(routes.toArray());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				Route route = (Route)value;
				label.setText(route.getDescription());
				label.setToolTipText(route.getId());
				if (isSelected) { // Is it the selected item in dropdown list?  
					label.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
					label.setForeground(UIManager.getColor("ComboBox.selectionForeground"));
				} else {
					label.setBackground(bg);
					label.setForeground(fg);
				}
				return label;
			}
		});
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				JList list = (JList)e.getSource();
				Route route = (Route)list.getSelectedValue();
				routeLine.setRoute(route);
				routeMap.setRoute(route);
			}
		});
		JScrollPane scroll = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroll, BorderLayout.WEST);

		routeMap = new RouteMapDrawer(stopPoints);
		panel.add(routeMap, BorderLayout.CENTER);

		pack();
		list.setSelectedIndex(0);
	}
	class RouteDrawer extends JPanel {
		private static final long serialVersionUID = 1L;

		Route route;

		public RouteDrawer() {
			setPreferredSize(new Dimension(-1, 100));
			setBackground(Color.WHITE);
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
			int lineThick = 6;
			int stopThick = 6;
			int stopHeight = 10;
			int textDistance = 10;
			g.setColor(bg);
			g.fillRect(stopWidth / 4, midHeight - lineThick / 2, getWidth() - stopWidth / 2, lineThick);

			for (int i = 0; i < stops.size(); ++i) {
				int left = stopWidth * i;
				int right = stopWidth * (i + 1);
				int midPos = (left + right) / 2;
				g.setColor(bg);
				if (i % 2 == 0) {
					g.fillRect(midPos - stopThick / 2, midHeight - stopHeight, stopThick, stopHeight); // above
				} else {
					g.fillRect(midPos - stopThick / 2, midHeight - 0, stopThick, stopHeight); // below
				}

				String name = stops.get(i).getName();
				int x = midPos - g.getFontMetrics().stringWidth(name) / 2;
				int y;
				if (i % 2 == 0) {
					y = midHeight - (stopHeight + textDistance) + 0 /* y == baseline */; // above
				} else {
					y = midHeight + (stopHeight + textDistance) + g.getFontMetrics().getAscent(); // below
				}
				g.setColor(Color.BLACK);
				g.drawString(name, x, y);
			}
		}
		public Route getRoute() {
			return route;
		}
		public void setRoute(Route route) {
			this.route = route;
			repaint();
		}
	}
	class RouteMapDrawer extends JPanel {
		private static final long serialVersionUID = 1L;

		private Route route;
		private final Set<StopPoint> stations;
		double minLat;
		double maxLat;
		double minLon;
		double maxLon;

		public RouteMapDrawer(Set<StopPoint> stations) {
			this.stations = stations;
			minMax();
		}

		protected void minMax() {
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
			int r = 8;
			double offX = minLon, offY = minLat;
			int width = getWidth(), height = getHeight();
			double scaleX = width / (maxLon - minLon);
			double scaleY = height / (maxLat - minLat);
			scaleX = scaleY = Math.min(scaleX, scaleY) * .90;
			int alignX = (width - (int)((maxLon - offX) * scaleX)) / 2;
			int alignY = -(height - (int)((maxLat - offY) * scaleY)) / 2;
			for (StopPoint station: stations) {
				Location loc = station.getLocation();
				int x = (int)((loc.getLongitude() - offX) * scaleX) + alignX;
				int y = height - (int)((loc.getLatitude() - offY) * scaleY) + alignY;
				g.setColor(bg);
				g.fillOval(x - r / 2, y - r / 2, r, r);
			}
			if (route == null) {
				return;
			}
			g2.setStroke(new BasicStroke(r / 2));
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					int fromX = (int)((link.getFrom().getLocation().getLongitude() - offX) * scaleX) + alignX;
					int fromY = height - (int)((link.getFrom().getLocation().getLatitude() - offY) * scaleY) + alignY;
					int toX = (int)((link.getTo().getLocation().getLongitude() - offX) * scaleX) + alignX;
					int toY = height - (int)((link.getTo().getLocation().getLatitude() - offY) * scaleY) + alignY;
					g.setColor(bg);
					g.drawLine(fromX, fromY, toX, toY);
				}
			}
		}

		public Route getRoute() {
			return route;
		}
		public void setRoute(Route route) {
			this.route = route;
			repaint();
		}
	}
}
