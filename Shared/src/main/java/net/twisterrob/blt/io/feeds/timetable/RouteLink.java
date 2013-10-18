package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.RouteLink.DirectionEnum;

public class RouteLink implements Iterable<StopPoint> {
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
		return String.format("%2$s -> %3$s {%1$s}", id, from.getName(), to.getName());
	}

	public Iterator<StopPoint> iterator() {
		return Arrays.asList(from, to).iterator();
	}
}