import java.io.*;
import java.util.*;

import javax.annotation.Nonnull;

import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.Route;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.RouteLink;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.RouteSection;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.StopPoint;
import net.twisterrob.blt.model.Line;

public class TravelNetworkParser {
	private static final String DATA_ROOT = "../temp/feed15/lultramdlrcablecarriver";
	@SuppressWarnings("serial") private static final Map<Line, String> FILES = new HashMap<Line, String>() {
		{
			put(Line.Bakerloo, "tfl_1-BAK_-2-y05.xml");
			put(Line.Central, "tfl_1-CEN_-670103-y05.xml");
			put(Line.Circle, "tfl_1-CIR_-290103-y05.xml");
			put(Line.District, "tfl_1-DIS_-1430113-y05.xml");
			put(Line.HammersmithAndCity, "tfl_1-HAM_-290103-y05.xml");
			put(Line.Jubilee, "tfl_1-JUB_-120140-y05.xml");
			put(Line.Metropolitan, "tfl_1-MET_-3330101-y05.xml");
			put(Line.Northern, "tfl_1-NTN_-530103-y05.xml");
			put(Line.Piccadilly, "tfl_1-PIC_-500101-y05.xml");
			put(Line.Victoria, "tfl_1-VIC_-350112-y05.xml");
			put(Line.WaterlooAndCity, "tfl_1-WAC_-60102-y05.xml");
			put(Line.DLR, "tfl_25-DLR_-6-y05.xml");
			put(Line.EmiratesAirline, "tfl_71-CABd-1-y05.xml");
			//put(Line.Overground, "");
		}
	};
	public static void main(String[] args) throws Throwable {
		@Nonnull
		Line line = Line.District;
		JourneyPlannerTimetableHandler handler = new JourneyPlannerTimetableHandler();
		File file = new File(DATA_ROOT, FILES.get(line));
		FileInputStream stream = new FileInputStream(file);
		JourneyPlannerTimetableFeed feed = handler.parse(stream);
		stream.close();
		List<Route> routes = feed.getRoutes();
		reconstruct(routes);
		new LineDisplay(line, routes).setVisible(true);
		//testAll();
	}
	private static @Nonnull
	List<Route> reconstruct(@Nonnull List<Route> routes) {
		Map<String, Node> nodes = new HashMap<String, Node>();
		for (StopPoint stop: JourneyPlannerTimetableFeed.getStopPoints(routes)) {
			nodes.put(stop.getName(), new Node(stop));
		}
		for (Route route: routes) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					StopPoint from = link.getFrom();
					StopPoint to = link.getTo();
					Node fromNode = nodes.get(from.getName());
					Node toNode = nodes.get(to.getName());
					fromNode.out.add(toNode);
					toNode.in.add(fromNode);
				}
			}
		}

		// TODO make these set
		List<Node> starts = new LinkedList<Node>();
		List<Node> ends = new LinkedList<Node>();
		List<Node> leafs = new LinkedList<Node>();
		for (Node node: nodes.values()) {
			if (node.in.isEmpty()) {
				starts.add(node);
			}
			if (node.out.isEmpty()) {
				ends.add(node);
			}
			if (node.in.equals(node.out)) {
				leafs.add(node);
			}
		}
		System.out.printf("Starts: %s\n", starts);
		System.out.printf("Ends: %s\n", ends);
		System.out.printf("Leafs: %s\n", leafs);

		return Collections.emptyList();
	}
	static class Node {
		@Nonnull StopPoint stop;
		final @Nonnull List<Node> in = new LinkedList<Node>();
		final @Nonnull List<Node> out = new LinkedList<Node>();
		public Node(@Nonnull StopPoint stop) {
			this.stop = stop;
		}

		@Override
		public String toString() {
			return String.format("%2$s", stop.getId(), stop.getName());
		}
	}

	protected static void testAll() throws Throwable {
		for (Line line: FILES.keySet()) {
			JourneyPlannerTimetableHandler handler = new JourneyPlannerTimetableHandler();
			File file = new File(DATA_ROOT, FILES.get(line));
			FileInputStream stream = new FileInputStream(file);
			JourneyPlannerTimetableFeed feed = handler.parse(stream);
			stream.close();
			for (Route route: feed.getRoutes()) {
				for (RouteSection section: route.getRouteSections()) {
					for (RouteLink link: section.getRouteLinks()) {
						if (link.getDistance() == 0) {
							System.out.println(link);
						}
					}
				}
			}
		}
	}
}
