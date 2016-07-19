package net.twisterrob.blt.data.algo.routes;

import java.util.*;

import net.twisterrob.blt.io.feeds.timetable.*;

public class FeedProcessor {
	public static List<Route> reconstruct(JourneyPlannerTimetableFeed feed) {
		RouteInfo info = new RouteInfo(feed.getRoutes());
		info.build();
		info.analyze();
		System.out.printf("\t\033[1;32mJunctions\033[0m: %s\n", info.getJunctions());
		Set<Node> starts = new TreeSet<>(info.getStarts());
		starts.removeAll(info.getLeaves());
		System.out.printf("\t\033[1;32mStart\033[0m: %s\n", starts);
		Set<Node> ends = new TreeSet<>(info.getEnds());
		starts.removeAll(info.getLeaves());
		System.out.printf("\t\033[1;32mEnd\033[0m: %s\n", ends);

		Set<Node> leaves = new TreeSet<>(info.getLeaves());
		leaves.removeAll(info.getStarts());
		leaves.removeAll(info.getEnds());
		System.out.printf("\t\033[1;32mLeaves\033[0m: %s\n", info.getLeaves());
		System.out.printf("\t\033[1;31mExtra leaves (without start/end)\033[0m: %s\n", leaves);

		//print(info.getNode("West Croydon Tram Stop"));

		for (Route route : feed.getRoutes()) {
			@SuppressWarnings({"unused", "deprecation"})
			Route newRoute = route.reconstruct();
		}

		return feed.getRoutes();
	}
}
