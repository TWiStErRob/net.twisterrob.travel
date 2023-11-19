package org.maptiler;

import java.util.Locale;

/** WGS84 coordinates, lat/lon, EPSG:4326 */
public class LatLon {
	public final double lat, lon;
	public LatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "(%s, %s)", lat, lon);
	}
}
