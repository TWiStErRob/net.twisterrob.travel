package net.twisterrob.android.utils.tostring.stringers.detailed;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class LatLngStringer extends Stringer<LatLng> {
	@Override public String getType(LatLng object) {
		return "LatLng";
	}
	@Override public void toString(@NonNull ToStringAppender append, LatLng latlng) {
		append.formattedProperty(null, null, "%f,%f", latlng.latitude, latlng.longitude);
	}
}
