package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

import javax.annotation.Nonnull;

import net.twisterrob.blt.io.feeds.BaseFeed;

public class JourneyPlannerTimetableFeed extends BaseFeed {
	private @Nonnull List<Route> routes = new ArrayList<Route>();

	@SuppressWarnings("null")
	@Nonnull
	public List<Route> getRoutes() {
		return Collections.unmodifiableList(routes);
	}
	protected void addRoute(@Nonnull Route route) {
		routes.add(route);
	}

	@Nonnull
	public static Set<StopPoint> getStopPoints(@Nonnull List<Route> routes) {
		Set<StopPoint> stopPoints = new TreeSet<StopPoint>(new Comparator<StopPoint>() {
			@Override
			public int compare(@Nonnull StopPoint o1, @Nonnull StopPoint o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		for (Route route: routes) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					stopPoints.add(link.getFrom());
					stopPoints.add(link.getTo());
				}
			}
		}
		return stopPoints;
	}

	@Override
	protected void postProcess() {
		super.postProcess();
		fixRoutes();
		fixDistances();
		collapseRoutes();
	}

	private void fixDistances() {
		for (Route route: routes) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					if (link.getDistance() == 0) {
						RouteLink reverse = findLink(link.getTo(), link.getFrom());
						if (reverse != null) {
							link.setDistance(reverse.getDistance());
						}
					}
				}
			}
		}
	}

	private RouteLink findLink(StopPoint from, StopPoint to) {
		for (Route route: routes) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					if (StopPoint.BY_NAME.compare(link.getFrom(), from) == 0
							&& StopPoint.BY_NAME.compare(link.getTo(), to) == 0) {
						return link;
					}
				}
			}
		}
		return null;
	}
	/**
	 * Somehow there are two 'Pudding Mill Lane' -> 'Blackwall' -> 'East India' -> 'Stratford' route section parts
	 * on {@link Line#DLR}, which is not possible in reality because there are no rails between these stations.
	 */
	private void fixRoutes() {
		for (Route route: routes) {
			for (RouteSection section: route.getRouteSections()) {
				RouteLink link1 = null;
				RouteLink link2 = null;
				RouteLink link3 = null;
				for (RouteLink link: section.getRouteLinks()) {
					link1 = link2;
					link2 = link3;
					link3 = link;
					if (true //
							&& link1 != null
							&& "Pudding Mill Lane".equals(link1.getFrom().getName())
							&& "Blackwall".equals(link1.getTo().getName())
							&& link2 != null
							&& "Blackwall".equals(link2.getFrom().getName())
							&& "East India".equals(link2.getTo().getName())
							&& link3 != null
							&& "East India".equals(link3.getFrom().getName())
							&& "Stratford".equals(link3.getTo().getName())) {
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
	public void collapseRoutes() {
		routes : for (Iterator<Route> iterator = routes.iterator(); iterator.hasNext();) {
			Route route = iterator.next();
			for (Route other: getRoutes()) {
				if (other != route && contains(other, route)) {
					iterator.remove();
					continue routes;
				}
			}
		}
	}

	public void collapseBidis() {
		reverseRoutes : for (Iterator<Route> iterator = routes.iterator(); iterator.hasNext();) {
			Route route = iterator.next();
			for (Route other: getRoutes()) {
				if (other != route && exactOpposite(other, route)) {
					other.setDescription(route.getDescription() + " (bidi)");
					iterator.remove();
					continue reverseRoutes;
				}
			}
		}
	}

	private static boolean contains(Route route1, Route route2) {
		Iterator<StopPoint> stops1 = route1.getStopPoints().iterator();
		Iterator<StopPoint> stops2 = route2.getStopPoints().iterator();
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
	protected static boolean same(StopPoint stop2, StopPoint current) {
		return current.getName().equals(stop2.getName());
	}
	private static boolean exactOpposite(Route route1, Route route2) {
		List<StopPoint> stopList1 = route1.getStopPoints();
		ListIterator<StopPoint> stops1 = stopList1.listIterator(0); // front
		List<StopPoint> stopList2 = route2.getStopPoints();
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
