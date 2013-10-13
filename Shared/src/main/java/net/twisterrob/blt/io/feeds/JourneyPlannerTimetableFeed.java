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
	}

}
