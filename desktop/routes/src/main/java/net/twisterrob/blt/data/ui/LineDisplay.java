package net.twisterrob.blt.data.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed;
import net.twisterrob.blt.io.feeds.timetable.Route;
import net.twisterrob.blt.io.feeds.timetable.StopPoint;
import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData;
import net.twisterrob.blt.model.Line;
import net.twisterrob.blt.model.LineColors;

public class LineDisplay extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final TrackerNetData trackerNetData = new TrackerNetData();

	protected JList<Route> list;
	protected RouteMapDrawer routeMap;
	protected RouteDrawer routeLine;
	protected List<String> highlights;

	public LineDisplay(JourneyPlannerTimetableFeed feed, LineColors lineColors, String... highlights) {
		this(feed.getLine(), feed.getRoutes(), lineColors, highlights);
	}
	public LineDisplay(final Line line, List<Route> routes, LineColors lineColors, String... highlights) {
		super(trackerNetData.getDisplayName(line));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1024, 800));

		this.highlights = Arrays.asList(highlights);

		Set<StopPoint> stopPoints = JourneyPlannerTimetableFeed.getStopPoints(routes);

		Container panel = getContentPane();
		panel.setLayout(new BorderLayout());

		routeLine = new RouteDrawer(lineColors, line, null, this.highlights);
		routeLine.setBorder(BorderFactory.createEmptyBorder(5, 35, 5, 35));
		panel.add(routeLine, BorderLayout.SOUTH);

		list = new JList<>(routes.toArray(new Route[routes.size()]));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new LineRouteCellRenderer(lineColors, line));
		list.addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				@SuppressWarnings("unchecked") JList<Route> list = (JList<Route>)e.getSource();
				Route route = list.getSelectedValue();
				routeLine.setRoute(route);
				routeMap.setRoute(route);
			}
		});
		JScrollPane scroll = new JScrollPane(list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scroll, BorderLayout.WEST);

		routeMap = new RouteMapDrawer(lineColors, stopPoints, line, null, this.highlights);
		routeMap.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(routeMap, BorderLayout.CENTER);

		pack();
		list.setSelectedIndex(0);
	}
	private static final class LineRouteCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 5981659366947795590L;

		private final Color fg;
		private final Color bg;

		LineRouteCellRenderer(LineColors colors, Line line) {
			fg = new Color(colors.getForeground(line));
			bg = new Color(colors.getBackground(line));
		}
		@Override public Component getListCellRendererComponent(JList<?> list, Object value, int index,
				boolean isSelected,
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
