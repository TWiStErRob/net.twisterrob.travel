package net.twisterrob.blt.android.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

import net.twisterrob.java.model.Location;

public class LocationUtils extends net.twisterrob.java.model.LocationUtils {
	private static final double GEO = 1e6d;

	// Maps V1 GeoPoint

	@Deprecated
	public static GeoPoint toGeoPoint(Location loc) {
		return new GeoPoint((int)(loc.getLatitude() * GEO), (int)(loc.getLongitude() * GEO));
	}

	@Deprecated
	public static Location fromGeoPoint(GeoPoint geo) {
		return new Location(geo.getLatitudeE6() / GEO, geo.getLongitudeE6() / GEO);
	}

	@Deprecated
	public static boolean near(GeoPoint geo, Location loc) {
		return near(loc, geo);
	}

	@Deprecated
	public static boolean near(Location loc, GeoPoint geo) {
		return Math.abs(geo.getLatitudeE6() / GEO - loc.getLatitude()) < DELTA
				&& Math.abs(geo.getLongitudeE6() / GEO - loc.getLongitude()) < DELTA;
	}

	// Maps V2 GeoPoint

	public static LatLng toLatLng(Location loc) {
		return new LatLng(loc.getLatitude(), loc.getLongitude());
	}

	public static Location fromLatLng(LatLng ll) {
		return new Location(ll.latitude, ll.longitude);
	}

	public static boolean near(LatLng ll, Location loc) {
		return near(loc, ll);
	}

	public static boolean near(Location loc, LatLng ll) {
		return Math.abs(ll.latitude - loc.getLatitude()) < DELTA && Math.abs(ll.longitude - loc.getLongitude()) < DELTA;
	}
}