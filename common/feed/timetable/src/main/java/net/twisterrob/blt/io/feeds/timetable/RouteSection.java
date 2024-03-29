package net.twisterrob.blt.io.feeds.timetable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

// FIXME extend with from-to instead of name
public class RouteSection implements Iterable<StopPoint> {
	private String id;
	List<RouteLink> routeLinks = new ArrayList<>();

	public String getId() {
		return id;
	}
	protected void setId(String id) {
		this.id = id;
	}

	public List<RouteLink> getLinks() {
		return Collections.unmodifiableList(routeLinks);
	}
	protected void addLink(RouteLink routeLink) {
		routeLinks.add(routeLink);
	}

	public StopPoint firstStop() {
		if (routeLinks.isEmpty()) {
			return null;
		}
		return routeLinks.get(0).getFrom();
	}
	public StopPoint lastStop() {
		if (routeLinks.isEmpty()) {
			return null;
		}
		return routeLinks.get(routeLinks.size() - 1).getTo();
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "%2$d links {%1$s}", id, routeLinks.size());
	}
	public Iterator<StopPoint> iterator() {
		return new StopPointIterators(routeLinks.iterator());
	}
}
