package net.twisterrob.blt.io.feeds.timetable;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed.RouteFixer;
import net.twisterrob.blt.model.Line;

/**
 * There's a route link 'Westferry' -> 'Canary Wharf DLR Station' which excludes 'West India Quay' on {@link Line#DLR},
 * because it's not stopping there all the time. It also has no distance.
 * Fix: find the link and replace it with an extended link including 'West India Quay' and distances from other routes.
 */
public class DLRWestferry2CanaryWharfFixer implements RouteFixer {
	public boolean matches(JourneyPlannerTimetableFeed feed) {
		return feed.getLine() == Line.DLR;
	}
	public void fix(JourneyPlannerTimetableFeed feed) {
		for (Route route : feed.getRoutes()) {
			section:
			for (RouteSection section : route.getSections()) {
				for (RouteLink link : section.getLinks()) {
					if ("Westferry".equals(link.getFrom().getName())
							&& "Canary Wharf DLR Station".equals(link.getTo().getName())) {
						fix(section, link, feed.getRoutes());
						continue section;
					}
				}
			}
		}
	}

	private static void fix(RouteSection badSection, RouteLink badLink, Iterable<Route> routes) {
		for (Route route : routes) {
			for (RouteSection section : route.getSections()) {
				RouteLink link1;
				RouteLink link2 = null;
				for (RouteLink link : section.getLinks()) {
					link1 = link2;
					link2 = link;
					if (isWestferry2CanaryWharf(link1, link2)) {
						int index = badSection.routeLinks.indexOf(badLink);
						StopPoint cwd = badLink.getTo();
						badLink.setTo(link1.getTo());
						badLink.setDistance(link1.getDistance());
						RouteLink wiq2cwd = new RouteLink();
						wiq2cwd.setFrom(link2.getFrom());
						wiq2cwd.setTo(cwd);
						wiq2cwd.setDistance(link2.getDistance());
						wiq2cwd.setDirection(badLink.getDirection());
						wiq2cwd.setId(badLink.getId() + "-fix");
						badSection.routeLinks.add(index + 1, wiq2cwd);
						return;
					}
				}
			}
		}
	}

	private static boolean isWestferry2CanaryWharf(RouteLink link1, RouteLink link2) {
		//noinspection PointlessBooleanExpression simpler formatting
		return true
				&& link1 != null
				&& "Westferry".equals(link1.getFrom().getName())
				&& "West India Quay".equals(link1.getTo().getName())
				&& link2 != null
				&& "West India Quay".equals(link2.getFrom().getName())
				&& "Canary Wharf DLR Station".equals(link2.getTo().getName());
	}
}
