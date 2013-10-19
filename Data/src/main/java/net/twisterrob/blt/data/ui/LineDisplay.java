package net.twisterrob.blt.data.ui;
import java.awt.*;
import java.util.*;
import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.event.*;

import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.*;

public class LineDisplay extends JFrame {
	private static final long serialVersionUID = 1L;
	protected JList<Route> list;
	protected RouteMapDrawer routeMap;
	protected RouteDrawer routeLine;
	protected List<String> highlights;

	public LineDisplay(JourneyPlannerTimetableFeed feed, String... highlights) {
		this(feed.getLine(), feed.getRoutes(), highlights);
	}
	public LineDisplay(final @Nonnull Line line, @Nonnull List<Route> routes, String... highlights) {
		super(line.getTitle());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1024, 800));

		this.highlights = Arrays.asList(highlights);

		Set<StopPoint> stopPoints = JourneyPlannerTimetableFeed.getStopPoints(routes);

		Container panel = getContentPane();
		panel.setLayout(new BorderLayout());

		routeLine = new RouteDrawer(line, null, this.highlights);
		panel.add(routeLine, BorderLayout.SOUTH);

		list = new JList<>(routes.toArray(new Route[routes.size()]));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new LineRouteCellRenderer(line));
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				JList<Route> list = (JList<Route>)e.getSource();
				Route route = list.getSelectedValue();
				routeLine.setRoute(route);
				routeMap.setRoute(route);
			}
		});
		JScrollPane scroll = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroll, BorderLayout.WEST);

		routeMap = new RouteMapDrawer(stopPoints, line, null, this.highlights);
		panel.add(routeMap, BorderLayout.CENTER);

		pack();
		list.setSelectedIndex(0);
	}
	private static final class LineRouteCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 5981659366947795590L;

		private final Color fg;
		private final Color bg;

		LineRouteCellRenderer(Line line) {
			@Nonnull
			LineColors colors = new TubeStatusPresentationLineColors();
			fg = new Color(line.getForeground(colors));
			bg = new Color(line.getBackground(colors));
		}
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
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
	}
}
