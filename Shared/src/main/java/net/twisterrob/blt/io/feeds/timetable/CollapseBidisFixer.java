package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed.RouteFixer;

public class CollapseBidisFixer implements RouteFixer {
	public boolean matches(JourneyPlannerTimetableFeed feed) {
		return true;
	}
	public void fix(JourneyPlannerTimetableFeed feed) {
		reverseRoutes : for (Iterator<Route> it = feed.routes.iterator(); it.hasNext();) {
			Route route = it.next();
			for (Route other: feed.getRoutes()) {
				if (other != route && exactOpposite(other, route)) {
					other.setDescription(route.getDescription() + " (bidi)");
					it.remove();
					continue reverseRoutes;
				}
			}
		}
	}

	private static boolean same(StopPoint stop1, StopPoint stop2) {
		return StopPoint.BY_NAME.compare(stop1, stop2) == 0;
	}

	private static boolean exactOpposite(Route route1, Route route2) {
		List<StopPoint> stopList1 = route1.getStops();
		ListIterator<StopPoint> stops1 = stopList1.listIterator(0); // front
		List<StopPoint> stopList2 = route2.getStops();
		ListIterator<StopPoint> stops2 = stopList2.listIterator(stopList2.size()); // back
		while (stops1.hasNext() && stops2.hasPrevious()) {
			StopPoint stop1 = stops1.next();
			StopPoint stop2 = stops2.previous();
			if (!same(stop1, stop2)) {
				return false; // mismatch in a stop
			}
		}
		if (stops1.hasNext() || stops2.hasPrevious()) {
			return false; // they don't have the same size
		}
		return true;
	}
}
