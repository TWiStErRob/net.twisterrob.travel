package net.twisterrob.blt.io.feeds.timetable;

import java.util.*;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeedXml.StopPoint.StopClassification.StopTypeEnum;
import net.twisterrob.java.model.Location;

public class StopPoint implements Comparable<StopPoint> {
	public static final Comparator<StopPoint> BY_ID = new Comparator<StopPoint>() {
		public int compare(StopPoint o1, StopPoint o2) {
			return o1.getId().compareTo(o2.getId());
		}
	};
	public static final Comparator<StopPoint> BY_NAME = new Comparator<StopPoint>() {
		public int compare(StopPoint o1, StopPoint o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
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

	@Override public String toString() {
		return String.format(Locale.ROOT, "%2$s @ %3$s {%1$s}", id, name, locality);
	}

	public int compareTo(StopPoint o) {
		return this.name.compareTo(o.name);
	}
	public static Iterable<Location> getLocations(final Iterable<StopPoint> stations) {
		return new Iterable<Location>() {
			public Iterator<Location> iterator() {
				return new Iterator<Location>() {
					Iterator<StopPoint> it = stations.iterator();
					public void remove() {
						throw new UnsupportedOperationException();
					}

					public Location next() {
						return it.next().getLocation();
					}

					public boolean hasNext() {
						return it.hasNext();
					}
				};
			}
		};
	}
}
