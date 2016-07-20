package net.twisterrob.blt.android.data;

import android.location.Address;
import android.support.annotation.*;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

import net.twisterrob.java.model.Location;
import net.twisterrob.java.utils.CollectionTools;

public class LocationUtils extends net.twisterrob.java.model.LocationUtils {
	private static final double GEO = 1e6d;

	// Maps V1 GeoPoint

	public static GeoPoint toGeoPoint(Location loc) {
		return new GeoPoint((int)(loc.getLatitude() * GEO), (int)(loc.getLongitude() * GEO));
	}

	public static Location fromGeoPoint(GeoPoint geo) {
		return new Location(geo.getLatitudeE6() / GEO, geo.getLongitudeE6() / GEO);
	}

	public static boolean near(GeoPoint geo, Location loc) {
		return near(loc, geo);
	}

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

	public static @Nullable String getVagueAddress(@Nullable Address address) {
		if (address == null) {
			return null;
		}
		String addressString = CollectionTools.coalesce(
				address.getSubLocality(), // Orsett
				getLocality(address), // Beckenham
				getLocalityFromAddressLine(address), // Lambeth, London
				address.getThoroughfare(), // Brackley Road | Sun Street Passage
				address.getSubAdminArea(), // Turrock
				getAdmin(address), // Greater London | Essex
				address.getCountryName()  // United Kingdom
		);
		if (addressString != null && address.getPostalCode() != null) {
			addressString = addressString + ", " + address.getPostalCode();
		}
		return addressString;
	}
	private static String getAdmin(@NonNull Address address) {
		String admin = address.getAdminArea();
		if ("London".equals(admin) || "Greater London".equals(admin)) {
			admin = null;
		}
		return admin;
	}
	private static String getLocalityFromAddressLine(@NonNull Address address) {
		String locality = null;
		if (address.getMaxAddressLineIndex() > 1) {
			locality = address.getAddressLine(1); // 0: number, street; 1: ward, city; 2: postcode
			if ("London".equals(locality)) {
				locality = null;
			}
			if (locality != null && locality.endsWith(", London")) {
				locality = locality.substring(0, locality.length() - 8);
			}
			if (locality != null && address.getCountryName() != null && locality.contains(address.getCountryName())) {
				locality = null;
			}
			if (locality != null && address.getPostalCode() != null && locality.contains(address.getPostalCode())) {
				locality = null;
			}
		}
		return locality;
	}
	private static String getLocality(@NonNull Address address) {
		String locality = address.getLocality();
		if ("London".equals(locality)) {
			locality = null;
		}
		return locality;
	}
}
