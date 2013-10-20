package net.twisterrob.blt.data.ui;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.blt.data.algo.*;
import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.Line;

import org.xml.sax.SAXException;

public class TravelNetworkParser {
	private static final String DATA_ROOT = "../temp/feed15/lultramdlrcablecarriver";
	@SuppressWarnings("serial") private static final Map<Line, List<String>> FILES = new HashMap<Line, List<String>>() {
		{
			put(Line.Bakerloo, Arrays.asList("tfl_1-BAK_-2-y05.xml"));
			put(Line.Central, Arrays.asList("tfl_1-CEN_-670103-y05.xml"));
			put(Line.Circle, Arrays.asList("tfl_1-CIR_-290103-y05.xml"));
			put(Line.District, Arrays.asList("tfl_1-DIS_-1430113-y05.xml"));
			put(Line.HammersmithAndCity, Arrays.asList("tfl_1-HAM_-290103-y05.xml"));
			put(Line.Jubilee, Arrays.asList("tfl_1-JUB_-120140-y05.xml"));
			put(Line.Metropolitan, Arrays.asList("tfl_1-MET_-3330101-y05.xml"));
			put(Line.Northern, Arrays.asList("tfl_1-NTN_-530103-y05.xml"));
			put(Line.Piccadilly, Arrays.asList("tfl_1-PIC_-500101-y05.xml"));
			put(Line.Victoria, Arrays.asList("tfl_1-VIC_-350112-y05.xml"));
			put(Line.WaterlooAndCity, Arrays.asList("tfl_1-WAC_-60102-y05.xml"));
			put(Line.DLR, Arrays.asList("tfl_25-DLR_-6-y05.xml"));
			put(Line.EmiratesAirline, Arrays.asList("tfl_71-CABd-1-y05.xml"));
			put(Line.Tram, Arrays.asList("tfl_63-TRM1-1-y05.xml", "tfl_63-TRM2-1-y05.xml", "tfl_63-TRM3-1-y05.xml",
					"tfl_63-TRM4-1-y05.xml"));
			//put(Line.Overground, "");
		}
	};
	public static void main(String[] args) throws Throwable {
		testAll(false, FILES.keySet());
	}

	protected static JourneyPlannerTimetableFeed readAndDisplay(boolean gui, File... files) throws IOException,
			SAXException, FileNotFoundException {
		JourneyPlannerTimetableHandler handler = new JourneyPlannerTimetableHandler();
		JourneyPlannerTimetableFeed feed = null;
		for (File file: files) {
			try (FileInputStream stream = new FileInputStream(file)) {
				JourneyPlannerTimetableFeed readFeed = handler.parse(stream);
				if (feed == null) {
					feed = readFeed;
				} else {
					feed = feed.merge(readFeed);
				}
			}
		}
		if (feed == null) {
			return null;
		}
		System.out.printf("\033[1;35m%s\033[0m (\033[35m%s\033[0m)\n", feed.getLine(), feed.getOperator()
				.getTradingName());
		List<Route> routes = reconstruct(feed);
		if (gui) {
			new LineDisplay(feed.getLine(), routes, "Centrale Tramlink Stop", "Reeves Corner",
					"Wellesley Road Tram Stop").setVisible(true);
		}
		return feed;
	}

	protected static void testAll(boolean display, Collection<Line> lines) throws Throwable {
		Map<StopPoint, Set<Line>> stops = new TreeMap<>(StopPoint.BY_NAME);
		for (Line line: lines) {
			List<String> fileNames = FILES.get(line);
			File[] files = new File[fileNames.size()];
			int currentFile = 0;
			for (String fileName: fileNames) {
				files[currentFile++] = new File(DATA_ROOT, fileName);
			}
			JourneyPlannerTimetableFeed feed = readAndDisplay(display, files);
			for (StopPoint stop: JourneyPlannerTimetableFeed.getStopPoints(feed.getRoutes())) {
				Set<Line> stopLines = stops.get(stop);
				if (stopLines == null) {
					stopLines = EnumSet.noneOf(Line.class);
					stops.put(stop, stopLines);
				}
				stopLines.add(line);
			}
		}
		try (PrintWriter out = new PrintWriter("LondonTravel.v1.data-Stop.sql", "utf-8")) {
			for (Entry<StopPoint, Set<Line>> entry: stops.entrySet()) {
				StopPoint stop = entry.getKey();
				Set<Line> stopLines = entry.getValue();
				out.printf("insert into Stop(_id, name, type, latitude, longitude, precision, locality) "
						+ "values(%1$d, '%2$s', %7$d, %3$.10f, %4$.10f, %5$d, '%6$s');\n", stop.getName().hashCode(),
						stop.getName().replaceAll("'", "''"), stop.getLocation().getLatitude(), stop.getLocation()
								.getLongitude(), stop.getPrecision(), stop.getLocality().getName()
								.replaceAll("'", "''"), stopLines.iterator().next().getDefaultStopType().ordinal());
			}
		}
		try (PrintWriter out = new PrintWriter("LondonTravel.v1.data-Line_Stop.sql", "utf-8")) {
			for (Entry<StopPoint, Set<Line>> entry: stops.entrySet()) {
				StopPoint stop = entry.getKey();
				Set<Line> stopLines = entry.getValue();
				for (Line line: stopLines) {
					out.printf("insert into Line_Stop(line, stop) values(%1$d, %2$d);\n", //
							line.ordinal(), stop.getName().hashCode());
				}
			}
		}
	}

	private static List<Route> reconstruct(JourneyPlannerTimetableFeed feed) {
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

		for (Route route: feed.getRoutes()) {
			@SuppressWarnings({"unused", "deprecation"})
			Route newRoute = route.reconstruct();
		}

		return feed.getRoutes();
	}

	protected static void print(Node node) {
		System.out.printf("\t\033[1;32mNode\033[0m: %s\n", node.getStop());
		System.out.printf("\t\t\033[1;36mNode-in\033[0m: %s\n", node.getIn());
		System.out.printf("\t\t\033[1;36mNode-out\033[0m: %s\n", node.getOut());
	}
	protected static void printNameGroups(RouteInfo info) {
		Map<String, Set<StopPoint>> groups = info.groupByName(false);
		for (Entry<String, Set<StopPoint>> o: groups.entrySet()) {
			for (StopPoint stop: o.getValue()) {
				System.out.printf("%3$s/%1$s,\"%2$s\"\n", stop.getId(), stop.getLocation(), o.getKey());
			}
		}
	}
}
