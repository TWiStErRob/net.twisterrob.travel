import java.io.*;
import java.util.*;

import javax.annotation.Nonnull;

import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.Route;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.RouteLink;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeed.RouteSection;
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
		new LineDisplay(line, feed.getRoutes()).setVisible(true);
		//testAll();
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
