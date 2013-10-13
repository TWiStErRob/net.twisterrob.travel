import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.Route;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.StopPoint;
import net.twisterrob.blt.model.*;

public class LineDisplay extends JFrame {
	private static final long serialVersionUID = 1L;
	private JList list;
	private RouteMapDrawer routeMap;
	private RouteDrawer routeLine;
	private Color fg;
	private Color bg;

	public LineDisplay(final Line line, JourneyPlannerTimetableFeed feed) {
		super(line.getTitle());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1024, 800));

		LineColors colors = new TubeStatusPresentationLineColors();
		fg = new Color(line.getForeground(colors));
		bg = new Color(line.getBackground(colors));

		Set<StopPoint> stopPoints = feed.getStopPoints();

		Container panel = getContentPane();
		panel.setLayout(new BorderLayout());

		routeLine = new RouteDrawer();
		panel.add(routeLine, BorderLayout.SOUTH);

		list = new JList(feed.getRoutes().toArray());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				Route route = (Route)value;
				label.setText(route.getDescription());
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
			}
		});
		JScrollPane scroll = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroll, BorderLayout.WEST);

		routeMap = new RouteMapDrawer(stopPoints);
		panel.add(routeMap, BorderLayout.CENTER);

		pack();
		list.setSelectedIndex(0);
		setVisible(true);
	}
	class RouteDrawer extends JPanel {
		private static final long serialVersionUID = 1L;

		Route route;

		public RouteDrawer() {
			setPreferredSize(new Dimension(-1, 100));
			setBackground(Color.WHITE);
		}

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

		public RouteMapDrawer(Set<StopPoint> stations) {
			this.stations = stations;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (route == null) {
				return;
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
