package net.twisterrob.blt.data.apps;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.blt.data.io.FeedReader;
import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.model.*;

import org.xml.sax.SAXException;

public class TravelNetworkParser {
	private static final DesktopStaticData STATIC_DATA = DesktopStaticData.INSTANCE;

	public static void main(String[] args) throws Throwable {
		writeDBScripts(STATIC_DATA.getTimetableFilenames().keySet());
	}

	protected static Map<StopPoint, Set<Line>> getStopsAndLines(Iterable<Line> lines) throws IOException, SAXException {
		Map<StopPoint, Set<Line>> stops = new TreeMap<>(StopPoint.BY_NAME);
		FeedReader reader = new FeedReader();
		String root = STATIC_DATA.getTimetableRoot();
		for (Line line: lines) {
			List<String> files = STATIC_DATA.getTimetableFilenames().get(line);
			JourneyPlannerTimetableFeed feed = reader.readFeed(root, files);
			System.out.printf("\033[1;35m%s\033[0m (\033[35m%s\033[0m)\n", feed.getLine(), feed.getOperator()
					.getTradingName());

			for (StopPoint stop: JourneyPlannerTimetableFeed.getStopPoints(feed.getRoutes())) {
				Set<Line> stopLines = stops.get(stop);
				if (stopLines == null) {
					stopLines = EnumSet.noneOf(Line.class);
					stops.put(stop, stopLines);
				}
				stopLines.add(line);
			}
		}
		return stops;
	}

	protected static void writeDBScripts(Iterable<Line> lines) throws Throwable {
		Map<StopPoint, Set<Line>> stops = getStopsAndLines(lines);
		try (
				PrintWriter outStop = new PrintWriter("LondonTravel.v1.data-Stop.sql", "utf-8");
				PrintWriter outLineStop = new PrintWriter("LondonTravel.v1.data-Line_Stop.sql", "utf-8");) {
			for (Entry<StopPoint, Set<Line>> entry: stops.entrySet()) {
				StopPoint stop = entry.getKey();
				Set<Line> stopLines = entry.getValue();
				writeStop(outStop, stop, stopLines.iterator().next().getDefaultStopType());
				for (Line line: stopLines) {
					writeStopLine(outLineStop, stop, line);
				}
			}
		}
	}

	protected static void writeStopLine(PrintWriter out, StopPoint stop, Line line) {
		out.printf("insert into Line_Stop(line, stop) values(%1$d, %2$d);\n", //
				line.ordinal(), stop.getName().hashCode());
	}

	protected static void writeStop(PrintWriter out, StopPoint stop, StopType stopType) {
		out.printf("insert into Stop(_id, name, type, latitude, longitude, precision, locality) "
				+ "values(%1$d, '%2$s', %7$d, %3$.10f, %4$.10f, %5$d, '%6$s');\n", stop.getName().hashCode(), stop
				.getName().replaceAll("'", "''"), stop.getLocation().getLatitude(), stop.getLocation().getLongitude(),
				stop.getPrecision(), stop.getLocality().getName().replaceAll("'", "''"), stopType.ordinal());
	}
}
