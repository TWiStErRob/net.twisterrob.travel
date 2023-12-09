package net.twisterrob.blt.io.feeds.timetable;

import java.util.Iterator;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed.RouteFixer;

public class CollapseRoutesFixer implements RouteFixer {
	public boolean matches(JourneyPlannerTimetableFeed feed) {
		return true;
	}

	public void fix(JourneyPlannerTimetableFeed feed) {
		routes:
		for (Iterator<Route> it = feed.routes.iterator(); it.hasNext(); ) {
			Route route = it.next();
			for (Route other : feed.getRoutes()) {
				if (other != route && contains(other, route)) {
					it.remove();
					continue routes;
				}
			}
		}
	}

	private static boolean contains(Route route1, Route route2) {
		Iterator<StopPoint> stops1 = route1.getStops().iterator();
		Iterator<StopPoint> stops2 = route2.getStops().iterator();
		if (!stops2.hasNext()) {
			return true; // empty route is in everything
		}

		// find first match
		StopPoint stop1 = null;
		StopPoint stop2 = stops2.next();
		while (stops1.hasNext()) {
			StopPoint current = stops1.next();
			if (same(stop2, current)) {
				stop1 = current;
				break;
			}
		}

		if (stop1 != null) {
			while (stops1.hasNext() && stops2.hasNext()) {
				stop1 = stops1.next();
				stop2 = stops2.next();
				if (!same(stop2, stop1)) {
					return false; // found a difference en-route
				}
			}
			if (stops2.hasNext()) {
				return false; // route2 has missing stops in route1
			}
			return true; // found a starting point and route2 is fully included in route1
		}
		return false; // didn't find a matching starting point
	}

	private static boolean same(StopPoint stop1, StopPoint stop2) {
		return StopPoint.BY_NAME.compare(stop1, stop2) == 0;
	}
}
