package net.twisterrob.blt.io.feeds.timetable;

import java.util.List;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed.RouteFixer;
import net.twisterrob.blt.model.Line;

/**
 * Somehow there are two 'Pudding Mill Lane' -> 'Blackwall' -> 'East India' -> 'Stratford' route section parts
 * on {@link Line#DLR}, which is not possible in reality because there are no rails between these stations.
 */
public class DLRPuddingMillLane2StratfordFixer implements RouteFixer {
	public boolean matches(JourneyPlannerTimetableFeed feed) {
		return feed.getLine() == Line.DLR;
	}
	public void fix(JourneyPlannerTimetableFeed feed) {
		for (Route route : feed.getRoutes()) {
			for (RouteSection section : route.getSections()) {
				RouteLink link1;
				RouteLink link2 = null;
				RouteLink link3 = null;
				for (RouteLink link : section.getLinks()) {
					link1 = link2;
					link2 = link3;
					link3 = link;
					if (isPuddingMillLane2Stratford(link1, link2, link3)) {
						List<RouteLink> links = section.routeLinks;
						StopPoint from = link1.getFrom();
						StopPoint to = link3.getTo();
						links.remove(link2);
						links.remove(link3);
						link1.setFrom(from);
						link1.setTo(to);
						break;
					}
				}
			}
		}
	}

	private static boolean isPuddingMillLane2Stratford(RouteLink link1, RouteLink link2, RouteLink link3) {
		//noinspection PointlessBooleanExpression simpler formatting
		return true
				&& link1 != null
				&& "Pudding Mill Lane".equals(link1.getFrom().getName())
				&& "Blackwall".equals(link1.getTo().getName())
				&& link2 != null
				&& "Blackwall".equals(link2.getFrom().getName())
				&& "East India".equals(link2.getTo().getName())
				&& link3 != null
				&& "East India".equals(link3.getFrom().getName())
				&& "Stratford".equals(link3.getTo().getName());
	}
}
