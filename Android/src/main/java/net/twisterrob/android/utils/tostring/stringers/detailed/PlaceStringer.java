package net.twisterrob.android.utils.tostring.stringers.detailed;

import java.util.*;

import javax.annotation.Nonnull;

import android.annotation.SuppressLint;

import com.google.android.gms.location.places.Place;

import net.twisterrob.android.annotation.PlaceType;
import net.twisterrob.java.utils.tostring.*;

public class PlaceStringer extends Stringer<Place> {
	@SuppressLint("VisibleForTests")
	@Override public void toString(@Nonnull ToStringAppender append, Place place) {
		append.identity(place.getId(), place.getName());
		append.complexProperty("locale", place.getLocale());
		append.complexProperty("address", place.getAddress());
		append.complexProperty("attributions", place.getAttributions());
		append.complexProperty("location", place.getLatLng());
		append.complexProperty("viewport", place.getViewport());
		append.complexProperty("phone", place.getPhoneNumber());
		append.complexProperty("website", place.getWebsiteUri());
		append.rawProperty("priceLevel", place.getPriceLevel());
		append.rawProperty("rating", place.getRating());
		Collection<String> placeTypes = new ArrayList<>();
		for (@PlaceType int placeType : place.getPlaceTypes()) {
			placeTypes.add(PlaceType.Converter.toString(placeType));
		}
		append.rawProperty("types", placeTypes);
	}
}
