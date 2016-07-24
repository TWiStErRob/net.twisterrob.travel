package net.twisterrob.android.utils.tostring.stringers.detailed;

import javax.annotation.Nonnull;

import com.google.android.gms.maps.model.LatLngBounds;

import net.twisterrob.java.utils.tostring.*;

public class LatLngBoundsStringer extends Stringer<LatLngBounds> {
	@Override public String getType(LatLngBounds object) {
		return "LatLngBounds";
	}
	@Override public void toString(@Nonnull ToStringAppender append, LatLngBounds bounds) {
		append.complexProperty("SW", bounds.southwest);
		append.complexProperty("NE", bounds.northeast);
	}
}
