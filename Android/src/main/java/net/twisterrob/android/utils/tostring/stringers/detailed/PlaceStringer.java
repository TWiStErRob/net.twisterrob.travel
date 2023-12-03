package net.twisterrob.android.utils.tostring.stringers.detailed;

import javax.annotation.Nonnull;

import android.annotation.SuppressLint;

import com.google.android.libraries.places.api.model.Place;

import net.twisterrob.java.utils.tostring.*;

public class PlaceStringer extends Stringer<Place> {
	@SuppressLint("VisibleForTests")
	@Override public void toString(@Nonnull ToStringAppender append, Place place) {
		append.identity(place.getId(), place.getName());
		// STOPSHIP review new properties
		append.complexProperty("address", place.getAddress());
		append.complexProperty("attributions", place.getAttributions());
		append.complexProperty("location", place.getLatLng());
		append.complexProperty("viewport", place.getViewport());
		append.complexProperty("phone", place.getPhoneNumber());
		append.complexProperty("website", place.getWebsiteUri());
		append.rawProperty("priceLevel", place.getPriceLevel());
		append.rawProperty("rating", place.getRating());
		append.rawProperty("types", place.getPlaceTypes());
	}
}
