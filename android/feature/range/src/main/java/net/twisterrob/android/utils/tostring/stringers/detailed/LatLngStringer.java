package net.twisterrob.android.utils.tostring.stringers.detailed;

import javax.annotation.Nonnull;

import com.google.android.gms.maps.model.LatLng;

import net.twisterrob.java.utils.tostring.*;

public class LatLngStringer extends Stringer<LatLng> {
	@Override public String getType(LatLng object) {
		return "LatLng";
	}
	@Override public void toString(@Nonnull ToStringAppender append, LatLng latlng) {
		append.formattedProperty(null, null, "%f,%f", latlng.latitude, latlng.longitude);
	}
}
