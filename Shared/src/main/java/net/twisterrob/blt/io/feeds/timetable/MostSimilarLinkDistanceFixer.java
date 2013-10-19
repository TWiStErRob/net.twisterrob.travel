package net.twisterrob.blt.io.feeds.timetable;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed.RouteFixer;

public class MostSimilarLinkDistanceFixer implements RouteFixer {
	public boolean matches(JourneyPlannerTimetableFeed feed) {
		for (Route route: feed.getRoutes()) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
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
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					if (link.getDistance() == 0) {
						RouteLink mostSimilar = findLink(feed.getRoutes(), link);
						if (mostSimilar != null) {
							link.setDistance(mostSimilar.getDistance());
						}
					}
				}
			}
		}
	}

	private static RouteLink findLink(Iterable<Route> routes, RouteLink badLink) {
		double badEstimated = badLink.getEstimatedDistance();
		double minDiff = Double.POSITIVE_INFINITY;
		RouteLink minLink = null;
		for (Route route: routes) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					if (link.getDistance() == 0) {
						continue;
					}
					double diff = Math.abs(badEstimated - link.getEstimatedDistance());
					if (diff < minDiff) {
						minDiff = diff;
						minLink = link;
					}
				}
			}
		}
		return minLink;
	}
}
