package net.twisterrob.blt.data.apps;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.blt.data.io.FeedReader;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.timetable.*;
import net.twisterrob.blt.io.feeds.trackernet.PredictionSummaryFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.Station;
import net.twisterrob.blt.model.*;

import org.slf4j.*;
import org.xml.sax.SAXException;

public class TravelNetworkParser {
	private static final Logger LOG = LoggerFactory.getLogger(TravelNetworkParser.class);

	private static final DesktopStaticData STATIC_DATA = DesktopStaticData.INSTANCE;

	public static void main(String[] args) throws Throwable {
		writeDBScripts(STATIC_DATA.getTimetableFilenames().keySet());
	}

	protected static Map<Line, JourneyPlannerTimetableFeed> getFeeds(Iterable<Line> lines) throws IOException,
			SAXException {
		Map<Line, JourneyPlannerTimetableFeed> feeds = new EnumMap<>(Line.class);
		FeedReader<JourneyPlannerTimetableFeed> reader = new FeedReader<>();
		String root = STATIC_DATA.getTimetableRoot();
		for (Line line: lines) {
			List<String> files = STATIC_DATA.getTimetableFilenames().get(line);
			JourneyPlannerTimetableFeed feed = reader.readFeed(Feed.JourneyPlannerTimetables, root, files);
			LOG.info("Read {} ({})", feed.getLine(), feed.getOperator().getTradingName());
			feeds.put(line, feed);
		}
		return feeds;
	}
	protected static Map<StopPoint, Set<Line>> getStopsAndLines(Map<Line, JourneyPlannerTimetableFeed> feeds) {
		Map<StopPoint, Set<Line>> stops = new TreeMap<>(StopPoint.BY_NAME);
		for (Entry<Line, JourneyPlannerTimetableFeed> entry: feeds.entrySet()) {
			Line line = entry.getKey();
			JourneyPlannerTimetableFeed feed = entry.getValue();
			LOG.info("Processing {} ({})", feed.getLine(), feed.getOperator().getTradingName());

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

	protected static Map<Line, Map<String, String>> getStationCodes(Iterable<Line> lines) throws IOException,
			SAXException {
		Map<Line, Map<String, String>> stopCodes = new TreeMap<>();
		FeedReader<PredictionSummaryFeed> reader = new FeedReader<>();
		String root = STATIC_DATA.getPredictionSummaryRoot();
		for (Line line: lines) {
			String fileName = STATIC_DATA.getPredictionSummaryFilenames().get(line);
			if (fileName == null) {
				continue;
			}
			File file = new File(root, fileName);
			LOG.info("Reading prediction feed for {} from {}", line, file);
			PredictionSummaryFeed feed = reader.readFeed(Feed.TubeDepartureBoardsPredictionSummary, file);
			feed.setLine(line);
			feed.applyAliases();
			feed.segregateAlienStations();
			LOG.info("Processing codes for {} ({})", feed.getLine(), feed.getTimeStamp());
			Map<String, String> stationMap = stopCodes.get(feed.getLine());
			if (stationMap == null) {
				stationMap = new TreeMap<>();
				stopCodes.put(line, stationMap);
			}
			for (Station station: feed.getStations()) {
				stationMap.put(station.getName(), station.getTrackerNetCode());
			}
		}
		return Line.fixMap(stopCodes, Collections.<String, String> emptyMap());
	}

	protected static void writeDBScripts(Collection<Line> lines) throws Throwable {
		Map<Line, JourneyPlannerTimetableFeed> feeds = getFeeds(lines);
		Map<StopPoint, Set<Line>> stops = getStopsAndLines(feeds);
		Map<Line, Map<String, String>> stationCodes = getStationCodes(lines);

		try (
				PrintWriter outStop = new PrintWriter("LondonTravel.v1.data-Stop.sql", "utf-8");
				PrintWriter outLineStop = new PrintWriter("LondonTravel.v1.data-Line_Stop.sql", "utf-8");) {
			LOG.info("Writing Stop and Line_Stop table contents for {}", lines);
			for (Entry<StopPoint, Set<Line>> entry: stops.entrySet()) {
				LOG.debug("Writing {} for {}", entry.getValue(), entry.getKey());
				StopPoint stop = entry.getKey();
				Set<Line> stopLines = entry.getValue();
				writeStop(outStop, stop, stopLines.iterator().next().getDefaultStopType());
				for (Line line: stopLines) {
					Map<String, String> codes = stationCodes.get(line);
					String code = codes.get(stop.getName());
					if (code == null) {
						LOG.warn("No code for {}/{}", line, stop.getName());
					} else {
						codes.remove(stop.getName());
					}
					writeStopLine(outLineStop, stop, line, code);
				}
			}
			for (Line line: lines) {
				for (Map.Entry<String, String> codes: stationCodes.get(line).entrySet()) {
					LOG.info("Remainder station {}/{} = {}", line, codes.getKey(), codes.getValue());
				}
			}
		}
	}

	protected static void writeStopLine(PrintWriter out, StopPoint stop, Line line, String code) {
		LOG.trace("StopLine: Line {}, station {} ({}), code {}", line, stop.getName(), stop.getName().hashCode(), code);
		code = code == null? "NULL" : "'" + code + "'";
		out.printf("insert into Line_Stop(line, stop, code) values(%1$d, %2$d, %3$s);\n", //
				line.ordinal(), stop.getName().hashCode(), code);
	}

	protected static void writeStop(PrintWriter out, StopPoint stop, StopType stopType) {
		LOG.trace("Stop {} ({}): {}", stop.getName(), stop.getName().hashCode(), stop);
		String locality = stop.getLocality().getName().replaceAll("'", "''");
		String name = stop.getName().replaceAll("'", "''");
		out.printf("insert into Stop(_id, name, type, latitude, longitude, precision, locality) "
				+ "values(%1$d, '%2$s', %7$d, %3$.10f, %4$.10f, %5$d, '%6$s');\n", stop.getName().hashCode(), name,
				stop.getLocation().getLatitude(), stop.getLocation().getLongitude(), stop.getPrecision(), locality,
				stopType.ordinal());
	}
}
