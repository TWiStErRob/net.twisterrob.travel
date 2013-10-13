package net.twisterrob.blt.io.feeds;

import java.util.*;

import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeedXml.RouteLink.DirectionEnum;
import net.twisterrob.blt.io.feeds.JourneyPlannerTimetableFeedXml.StopPoint.StopClassification.StopTypeEnum;
import net.twisterrob.java.model.Location;

public class JourneyPlannerTimetableFeed extends BaseFeed {
	private List<Route> routes = new ArrayList<Route>();

	public List<Route> getRoutes() {
		return Collections.unmodifiableList(routes);
	}
	protected void addRoute(Route route) {
		routes.add(route);
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
		private List<RouteLink> routeLinks = new ArrayList<RouteLink>();

		public String getId() {
			return id;
		}
		protected void setId(String id) {
			this.id = id;
		}

		public List<RouteLink> getRouteLinks() {
			return Collections.unmodifiableList(routeLinks);
		}
		protected void addLink(RouteLink routeLink) {
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
		private List<RouteSection> routeSections = new ArrayList<RouteSection>();
		private String description;

		public String getId() {
			return id;
		}
		protected void setId(String id) {
			this.id = id;
		}

		public List<RouteSection> getRouteSections() {
			return Collections.unmodifiableList(routeSections);
		}
		protected void addSection(RouteSection routeSection) {
			routeSections.add(routeSection);
		}

		public String getDescription() {
			return description;
		}
		protected void setDescription(String description) {
			this.description = description;
		}

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
	public Set<StopPoint> getStopPoints() {
		Set<StopPoint> stopPoints = new TreeSet<StopPoint>(new Comparator<StopPoint>() {
			@Override
			public int compare(StopPoint o1, StopPoint o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});
		for (Route route: getRoutes()) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					stopPoints.add(link.getFrom());
					stopPoints.add(link.getTo());
				}
			}
		}
		return stopPoints;
	}
}
