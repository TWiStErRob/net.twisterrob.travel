package net.twisterrob.blt.io.feeds.timetable;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed.RouteFixer;

public class ReverseLinkDistanceFixer implements RouteFixer {
	public boolean matches(JourneyPlannerTimetableFeed feed) {
		for (Route route: feed.getRoutes()) {
			for (RouteSection section: route.getSections()) {
				for (RouteLink link: section.getLinks()) {
					if (link.getDistance() == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void fix(JourneyPlannerTimetableFeed feed) {
		for (Route route: feed.getRoutes()) {
			for (RouteSection section: route.getSections()) {
				for (RouteLink link: section.getLinks()) {
					if (link.getDistance() == 0) {
						RouteLink reverse = findLink(feed.getRoutes(), link.getTo(), link.getFrom());
						if (reverse != null) {
							link.setDistance(reverse.getDistance());
						}
					}
				}
			}
		}
	}

	private static RouteLink findLink(Iterable<Route> routes, StopPoint from, StopPoint to) {
		for (Route route: routes) {
			for (RouteSection section: route.getSections()) {
				for (RouteLink link: section.getLinks()) {
					if (StopPoint.BY_NAME.compare(link.getFrom(), from) == 0
							&& StopPoint.BY_NAME.compare(link.getTo(), to) == 0) {
						return link;
					}
				}
			}
		}
		return null;
	}
}
