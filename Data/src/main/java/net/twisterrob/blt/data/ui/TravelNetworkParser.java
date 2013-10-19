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
			put(Line.Tram, "tfl_63-TRM1-1-y05.xml");
			//put(Line.Overground, "");
		}
	};
	public static void main(String[] args) throws Throwable {
		readAndDisplay(new File(DATA_ROOT, FILES.get(Line.Tram)), true);
		testAll();
	}

	protected static void readAndDisplay(File file, boolean gui) throws IOException, SAXException,
			FileNotFoundException {
		JourneyPlannerTimetableHandler handler = new JourneyPlannerTimetableHandler();
		try (FileInputStream stream = new FileInputStream(file)) {
			JourneyPlannerTimetableFeed feed = handler.parse(stream);
			System.out.printf("\033[1;35m%s\033[0m (\033[35m%s\033[0m)\n", //
					feed.getLine(), feed.getOperator().getTradingName());
			List<Route> routes = reconstruct(feed);
			if (gui) {
				new LineDisplay(feed.getLine(), routes, "Turnham Green").setVisible(true);
			}
		}
	}

	protected static void testAll() throws Throwable {
		for (Line line: FILES.keySet()) {
			File file = new File(DATA_ROOT, FILES.get(line));
			readAndDisplay(file, false);
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
