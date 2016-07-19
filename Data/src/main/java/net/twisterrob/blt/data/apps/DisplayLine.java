package net.twisterrob.blt.data.apps;

import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.blt.data.algo.routes.*;
import net.twisterrob.blt.data.io.FeedReader;
import net.twisterrob.blt.data.ui.LineDisplay;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.Line;

public class DisplayLine {
	private static final DesktopStaticData STATIC_DATA = DesktopStaticData.INSTANCE;
	public static void main(String[] args) throws Throwable {
		FeedReader<JourneyPlannerTimetableFeed> reader = new FeedReader<>();
		Map<Line, List<String>> fileNames = STATIC_DATA.getTimetableFilenames();
		List<String> lineFileNames = fileNames.get(Line.Circle);
		JourneyPlannerTimetableFeed feed = reader.readFeed(Feed.JourneyPlannerTimetables,
				STATIC_DATA.getTimetableRoot(), lineFileNames);
		System.out.printf("\033[1;35m%s\033[0m (\033[35m%s\033[0m)\n",
				feed.getLine(), feed.getOperator().getTradingName());
		List<Route> routes = FeedProcessor.reconstruct(feed);
		new LineDisplay(feed.getLine(), routes, "Centrale Tramlink Stop", "Reeves Corner", "Wellesley Road Tram Stop")
				.setVisible(true);
	}

	protected static void print(Node node) {
		System.out.printf("\t\033[1;32mNode\033[0m: %s\n", node.getStop());
		System.out.printf("\t\t\033[1;36mNode-in\033[0m: %s\n", node.getIn());
		System.out.printf("\t\t\033[1;36mNode-out\033[0m: %s\n", node.getOut());
	}

	protected static void printNameGroups(RouteInfo info) {
		Map<String, Set<StopPoint>> groups = info.groupByName(false);
		for (Entry<String, Set<StopPoint>> o : groups.entrySet()) {
			for (StopPoint stop : o.getValue()) {
				System.out.printf("%3$s/%1$s,\"%2$s\"\n", stop.getId(), stop.getLocation(), o.getKey());
			}
		}
	}
}
