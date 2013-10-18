package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

import javax.annotation.Nonnull;

public class RouteSection implements Iterable<StopPoint> {
	private String id;
	@Nonnull List<RouteLink> routeLinks = new ArrayList<RouteLink>();

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

	public StopPoint firstStop() {
		if (routeLinks.isEmpty()) {
			return null;
		}
		return routeLinks.listIterator(0).next().getFrom();
	}
	public StopPoint lastStop() {
		if (routeLinks.isEmpty()) {
			return null;
		}
		return routeLinks.listIterator(routeLinks.size()).previous().getTo();
	}

	@Override
	public String toString() {
		return String.format("%2$d links {%1$s}", id, routeLinks.size());
	}
	public Iterator<StopPoint> iterator() {
		return new StopPointIterators(routeLinks.iterator());
	}
}