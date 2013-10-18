package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

import javax.annotation.Nonnull;

public class Route implements Iterable<StopPoint> {
	private String id;
	private @Nonnull List<RouteSection> routeSections = new ArrayList<RouteSection>();
	private String description;

	public String getId() {
		return id;
	}
	protected void setId(String id) {
		this.id = id;
	}

	protected void addSection(@Nonnull RouteSection routeSection) {
		routeSections.add(routeSection);
	}
	@SuppressWarnings("null")
	@Nonnull
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
		return String.format("%2$s {%1$s}", id, description);
	}
}