package net.twisterrob.android.utils.tostring.stringers.detailed;

import com.google.android.gms.maps.model.LatLngBounds;

import androidx.annotation.NonNull;

import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class LatLngBoundsStringer extends Stringer<LatLngBounds> {
	@Override public String getType(LatLngBounds object) {
		return "LatLngBounds";
	}
	@Override public void toString(@NonNull ToStringAppender append, LatLngBounds bounds) {
		append.complexProperty("SW", bounds.southwest);
		append.complexProperty("NE", bounds.northeast);
	}
}
