package net.twisterrob.travel.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;

import androidx.annotation.NonNull;

import net.twisterrob.android.permissions.PermissionInterrogator;

public class MapUtils {

	@SuppressLint("MissingPermission") // It's declared in manifest and checked right here.
	public static void setMyLocationEnabledIfPossible(Activity activity, @NonNull GoogleMap map) {
		PermissionInterrogator interrogator = new PermissionInterrogator(activity);
		boolean hasFine = interrogator.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
		boolean hasCoarse = interrogator.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
		map.setMyLocationEnabled(hasFine || hasCoarse);
	}
}
