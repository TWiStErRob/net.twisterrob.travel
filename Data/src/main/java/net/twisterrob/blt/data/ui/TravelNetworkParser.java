package net.twisterrob.blt.data.ui;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import net.twisterrob.blt.data.algo.RouteInfo;
import net.twisterrob.blt.io.feeds.timetable.*;
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
		Line line = Line.Piccadilly;
		JourneyPlannerTimetableHandler handler = new JourneyPlannerTimetableHandler();
		File file = new File(DATA_ROOT, FILES.get(line));
		FileInputStream stream = new FileInputStream(file);
		JourneyPlannerTimetableFeed feed;
		try {
			feed = handler.parse(stream);
		} finally {
			stream.close();
		}
		List<Route> routes = feed.getRoutes();
		//routes = reconstruct(routes);
		new LineDisplay(line, routes, "Turnham Green").setVisible(true);
		testAll();
	}
	private static @Nonnull
	List<Route> reconstruct(@Nonnull List<Route> routes) {
		RouteInfo info = new RouteInfo(routes);
		info.build();
		//info.setPrintProgress("   ");
		info.analyze();
		System.out.printf("\033[1;32mLeafs\033[0m: %s\n", info.getLeafs());
		System.out.printf("\033[1;32mJunctions\033[0m: %s\n", info.getJunctions());

		return Collections.emptyList();
	}
	protected static void printNameGroups(RouteInfo info) {
		Map<String, Set<StopPoint>> groups = info.groupByName(false);
		for (Entry<String, Set<StopPoint>> o: groups.entrySet()) {
			for (StopPoint stop: o.getValue()) {
				System.out.printf("%3$s/%1$s,\"%2$s\"\n", stop.getId(), stop.getLocation(), o.getKey());
			}
		}
	}

	protected static void testAll() throws Throwable {
		for (Line line: FILES.keySet()) {
			JourneyPlannerTimetableHandler handler = new JourneyPlannerTimetableHandler();
			File file = new File(DATA_ROOT, FILES.get(line));
			FileInputStream stream = new FileInputStream(file);
			JourneyPlannerTimetableFeed feed;
			try {
				feed = handler.parse(stream);
				System.out.printf("\033[1;35m%s\033[0m (\033[35m%s\033[0m)\n", //
						feed.getLine(), feed.getOperator().getTradingName());
			} finally {
				stream.close();
			}
			List<Route> routes = feed.getRoutes();
			reconstruct(routes);
			for (Route route: routes) {
				for (RouteSection section: route.getRouteSections()) {
					for (RouteLink link: section.getRouteLinks()) {
						if (link.getDistance() == 0) {
							System.out.printf("\033[1;31m0 distance\033[0m: %s\n", link);
						}
					}
				}
			}
		}
	}
}
