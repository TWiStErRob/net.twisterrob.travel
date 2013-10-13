import java.io.*;

import net.twisterrob.blt.io.feeds.*;

public class TravelNetworkParser {
	public static void main(String[] args) throws Throwable {
		JourneyPlannerTimetableHandler handler = new JourneyPlannerTimetableHandler();
		JourneyPlannerTimetableFeed feed = handler.parse(new FileInputStream(new File("src/data/temp",
				"feed15/lultramdlrcablecarriver/tfl_1-WAC_-60102-y05.xml")));
		System.out.println(feed);
	}
}
