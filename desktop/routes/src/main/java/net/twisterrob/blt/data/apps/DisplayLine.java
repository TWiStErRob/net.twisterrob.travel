package net.twisterrob.blt.data.apps;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.twisterrob.blt.data.algo.routes.FeedProcessor;
import net.twisterrob.blt.data.algo.routes.Node;
import net.twisterrob.blt.data.algo.routes.RouteInfo;
import net.twisterrob.blt.data.io.FeedReader;
import net.twisterrob.blt.data.statics.DesktopHardcodedStaticData;
import net.twisterrob.blt.data.ui.LineDisplay;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed;
import net.twisterrob.blt.io.feeds.timetable.Route;
import net.twisterrob.blt.io.feeds.timetable.StopPoint;
import net.twisterrob.blt.model.Line;
import net.twisterrob.blt.model.LineColors;

public class DisplayLine {
	private static DesktopStaticData STATIC_DATA;
	public static void main(String[] args) throws Throwable {
		System.out.println(Arrays.asList(args));
		if (args.length < 3) {
			System.err.println("Usage: DisplayLine <timetableRoot> <predictionRoot>");
			System.exit(2);
		}
		File timetableRoot = new File(args[args.length - 2]);
		File predictionRoot = new File(args[args.length - 1]);
		STATIC_DATA = new DesktopHardcodedStaticData(timetableRoot, predictionRoot);
		List<String> lines = new ArrayList<>(Arrays.asList(args));
		lines.remove(lines.size() - 1);
		lines.remove(lines.size() - 1);
		for (String arg : lines) {
			Line line = Line.valueOf(arg);
			display(line);
		}
	}

	private static void display(Line line) throws java.io.IOException, org.xml.sax.SAXException {
		FeedReader<JourneyPlannerTimetableFeed> reader = new FeedReader<>();
		Map<Line, List<String>> fileNames = STATIC_DATA.getTimetableFilenames();
		List<String> lineFileNames = fileNames.get(line);
		JourneyPlannerTimetableFeed feed = reader.readFeed(Feed.JourneyPlannerTimetables,
				STATIC_DATA.getTimetableRoot(), lineFileNames);
		System.out.printf(Locale.ROOT, "\033[1;35m%s\033[0m (\033[35m%s\033[0m)%n",
				feed.getLine(), feed.getOperator().getTradingName());
		List<Route> routes = FeedProcessor.reconstruct(feed);
		String[] highlights = { "Centrale Tramlink Stop", "Reeves Corner", "Wellesley Road Tram Stop" };
		new LineDisplay(feed.getLine(), routes, new LineColors(STATIC_DATA.getLineColors()), highlights)
				.setVisible(true);
	}

	protected static void print(Node node) {
		System.out.printf(Locale.ROOT, "\t\033[1;32mNode\033[0m: %s%n", node.getStop());
		System.out.printf(Locale.ROOT, "\t\t\033[1;36mNode-in\033[0m: %s%n", node.getIn());
		System.out.printf(Locale.ROOT, "\t\t\033[1;36mNode-out\033[0m: %s%n", node.getOut());
	}

	protected static void printNameGroups(RouteInfo info) {
		Map<String, Set<StopPoint>> groups = info.groupByName(false);
		for (Entry<String, Set<StopPoint>> o : groups.entrySet()) {
			for (StopPoint stop : o.getValue()) {
				System.out.printf(Locale.ROOT, "%3$s/%1$s,\"%2$s\"%n", stop.getId(), stop.getLocation(), o.getKey());
			}
		}
	}
}
