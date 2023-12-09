package net.twisterrob.blt.android.db.model;

import net.twisterrob.java.model.Location;

public class AreaHullPoint {
	private final Location location;

	public AreaHullPoint(double latitude, double longitude) {
		location = new Location(latitude, longitude);
	}

	public Location getLocation() {
		return location;
	}
}
