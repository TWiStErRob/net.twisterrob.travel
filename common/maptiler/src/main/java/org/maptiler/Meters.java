package org.maptiler;

import java.util.Locale;

/** Spherical Mercator, XY in metres, EPSG:900913 */
public class Meters {
	public final double x, y;
	public Meters(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "(%s, %s)", x, y);
	}
}
