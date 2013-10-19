package net.twisterrob.blt.data.ui;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

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
		testAll(true, Arrays.asList(Line.Tram));
	}

	protected static void readAndDisplay(boolean gui, File... files) throws IOException, SAXException,
			FileNotFoundException {
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
		System.out.printf("\033[1;35m%s\033[0m (\033[35m%s\033[0m)\n", feed.getLine(), feed.getOperator()
				.getTradingName());
		List<Route> routes = reconstruct(feed);
		if (gui) {
			new LineDisplay(feed.getLine(), routes, "Turnham Green").setVisible(true);
		}
	}

	protected static void testAll(boolean display, Collection<Line> lines) throws Throwable {
		for (Line line: lines) {
			List<String> fileNames = FILES.get(line);
			File[] files = new File[fileNames.size()];
			int currentFile = 0;
			for (String fileName: fileNames) {
				files[currentFile++] = new File(DATA_ROOT, fileName);
			}
			readAndDisplay(display, files);
		}
	}

	private static @Nonnull
	List<Route> reconstruct(@Nonnull JourneyPlannerTimetableFeed feed) {
		RouteInfo info = new RouteInfo(feed.getRoutes());
		info.build();
		info.analyze();
		System.out.printf("\033[1;32mLeafs\033[0m: %s\n", info.getLeafs());
		System.out.printf("\033[1;32mJunctions\033[0m: %s\n", info.getJunctions());
		Set<Node> starts = info.getStarts();
		System.out.printf("\033[1;32mStart\033[0m: %s\n", starts);
		System.out.printf("\033[1;32mEnd\033[0m: %s\n", info.getEnds());

		return feed.getRoutes();
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
