package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

public class Route implements Iterable<StopPoint> {
	private String id;
	private List<RouteSection> routeSections = new ArrayList<>();
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
	public List<RouteSection> getSections() {
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

	public List<StopPoint> getStops() {
		List<StopPoint> stopPoints = new ArrayList<>();
		for (RouteSection section : getSections()) {
			RouteLink last = null;
			for (RouteLink link : section.getLinks()) {
				stopPoints.add(link.getFrom());
				last = link;
			}
			if (last != null) {
				stopPoints.add(last.getTo());
			}
		}
		return stopPoints;
	}

	public List<RouteLink> getLinks() {
		List<RouteLink> links = new ArrayList<>();
		for (RouteSection section : getSections()) {
			for (RouteLink link : section.getLinks()) {
				links.add(link);
			}
		}
		return links;
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "%2$s {%1$s}", id, description);
	}

	/**
	 * @deprecated TODO finish
	 */
	@SuppressWarnings("unused")
	@Deprecated
	public Route reconstruct() {
		List<StopPoint> stops = getStops();
		StopPoint first = firstStop();
		StopPoint last = lastStop();
		int sectionNum = 0;
		RouteSection currentSection = new RouteSection();
		currentSection.setId(String.valueOf(sectionNum++));
		for (StopPoint stop : stops) {
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
