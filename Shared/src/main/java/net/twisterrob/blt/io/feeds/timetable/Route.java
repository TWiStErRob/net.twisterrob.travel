package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

public class Route implements Iterable<StopPoint> {
	private String id;
	private List<RouteSection> routeSections = new ArrayList<RouteSection>();
	private String description;

	public String getId() {
		return id;
	}
	protected void setId(String id) {
		this.id = id;
	}

	protected void addSection(RouteSection routeSection) {
		routeSections.add(routeSection);
	}
	public List<RouteSection> getRouteSections() {
		return Collections.unmodifiableList(routeSections);
	}
	public Iterator<StopPoint> iterator() {
		return new StopPointIterators(routeSections.iterator());
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

	public List<RouteLink> getRouteLinks() {
		List<RouteLink> links = new ArrayList<RouteLink>();
		for (RouteSection section: getRouteSections()) {
			for (RouteLink link: section.getRouteLinks()) {
				links.add(link);
			}
		}
		return links;
	}

	@Override
	public String toString() {
		return String.format("%2$s {%1$s}", id, description);
	}

	/**
	 * @deprecated TODO finish
	 */
	@SuppressWarnings("unused")
	@Deprecated
	public Route reconstruct() {
		List<StopPoint> stops = getStopPoints();
		StopPoint first = firstStop();
		StopPoint last = lastStop();
		int sectionNum = 0;
		RouteSection currentSection = new RouteSection();
		currentSection.setId(String.valueOf(sectionNum++));
		for (StopPoint stop: stops) {
			RouteLink link = new RouteLink();
		}
		return this;
	}
	protected StopPoint firstStop() {
		if (routeSections.isEmpty()) {
			return null;
		}
		return routeSections.get(0).firstStop();
	}
	protected StopPoint lastStop() {
		if (routeSections.isEmpty()) {
			return null;
		}
		return routeSections.get(routeSections.size() - 1).lastStop();
	}

}