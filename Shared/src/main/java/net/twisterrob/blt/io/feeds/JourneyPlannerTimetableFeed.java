package net.twisterrob.blt.io.feeds;

import java.util.*;

import javax.annotation.Nonnull;

import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeedXml.RouteLink.DirectionEnum;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeedXml.StopPoint.StopClassification.StopTypeEnum;
import net.twisterrob.java.model.Location;

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
	void postProcess() {
		super.postProcess();
		collapseRoutes();
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
					other.setDescription(route.getDescription() + " [bidi]");
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
	public static class Locality {
		private String id;
		private String name;

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return String.format("%2$s [%1$s]", id, name);
		}
	}
	public static class StopPoint {
		private String id;
		private String name;
		private Locality locality;
		private Location location;
		private int precisionMeters;
		private StopTypeEnum type;

		public String getId() {
			return id;
		}
		protected void setId(String id) {
			this.id = id;

		}
		public String getName() {
			return name;
		}
		protected void setName(String name) {
			this.name = name;
		}

		public Locality getLocality() {
			return locality;
		}
		protected void setLocality(Locality locality) {
			this.locality = locality;
		}

		public Location getLocation() {
			return location;
		}
		protected void setLocation(Location location) {
			this.location = location;
		}

		public int getPrecision() {
			return precisionMeters;
		}
		protected void setPrecision(int meters) {
			this.precisionMeters = meters;
		}

		public StopTypeEnum getType() {
			return type;
		}
		protected void setType(StopTypeEnum type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return String.format("%2$s @ %3$s [%1$s]", id, name, locality);
		}
	}
	public static class RouteSection {
		private String id;
		private @Nonnull List<RouteLink> routeLinks = new ArrayList<RouteLink>();

		public String getId() {
			return id;
		}
		protected void setId(String id) {
			this.id = id;
		}

		@SuppressWarnings("null")
		@Nonnull
		public List<RouteLink> getRouteLinks() {
			return Collections.unmodifiableList(routeLinks);
		}
		protected void addLink(@Nonnull RouteLink routeLink) {
			routeLinks.add(routeLink);
		}

		@Override
		public String toString() {
			return String.format("%2$d links [%1$s]", id, routeLinks.size());
		}
	}
	public static class RouteLink {
		private String id;
		private int distance;
		private DirectionEnum direction;
		private StopPoint from;
		private StopPoint to;

		public String getId() {
			return id;
		}
		protected void setId(String id) {
			this.id = id;
		}

		public int getDistance() {
			return distance;
		}
		protected void setDistance(int distance) {
			this.distance = distance;
		}

		public DirectionEnum getDirection() {
			return direction;
		}
		protected void setDirection(DirectionEnum direction) {
			this.direction = direction;
		}

		public StopPoint getFrom() {
			return from;
		}
		protected void setFrom(StopPoint from) {
			this.from = from;
		}

		public StopPoint getTo() {
			return to;
		}
		protected void setTo(StopPoint to) {
			this.to = to;
		}

		@Override
		public String toString() {
			return String.format("%2$s -> %3$s [%1$s]", id, from.getName(), to.getName());
		}
	}
	public static class Route {
		private String id;
		private @Nonnull List<RouteSection> routeSections = new ArrayList<RouteSection>();
		private String description;

		public String getId() {
			return id;
		}
		protected void setId(String id) {
			this.id = id;
		}

		@SuppressWarnings("null")
		@Nonnull
		public List<RouteSection> getRouteSections() {
			return Collections.unmodifiableList(routeSections);
		}
		protected void addSection(@Nonnull RouteSection routeSection) {
			routeSections.add(routeSection);
		}

		public String getDescription() {
			return description;
		}
		protected void setDescription(String description) {
			this.description = description;
		}

		@Nonnull
		public List<StopPoint> getStopPoints() {
			List<StopPoint> stopPoints = new ArrayList<StopPoint>();
			for (RouteSection section: getRouteSections()) {
				RouteLink last = null;
				for (RouteLink link: section.getRouteLinks()) {
					stopPoints.add(link.getFrom());
					last = link;
				}
				if (last != null) {
					stopPoints.add(last.getTo());
				}
			}
			return stopPoints;
		}

		@Override
		public String toString() {
			return String.format("%2$s [%1$s]", id, description);
		}
	}
}
