package net.twisterrob.android.utils.tostring.stringers.detailed;

import java.util.List;

import android.annotation.SuppressLint;

import com.google.android.libraries.places.api.model.Place;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class PlaceStringer extends Stringer<Place> {

	@SuppressLint("VisibleForTests")
	@Override public void toString(@NonNull ToStringAppender append, Place place) {
		append.identity(place.getId(), place.getName());

		append.rawProperty("latLng", place.getLatLng()); // LatLng
		append.rawProperty("address", place.getAddress()); // String
		append.rawProperty("addressComponents", place.getAddressComponents()); // AddressComponents
		append.rawProperty("viewport", place.getViewport()); // LatLngBounds
		append.rawProperty("plusCode", place.getPlusCode()); // PlusCode

		append.rawProperty("phoneNumber", place.getPhoneNumber()); // String
		append.rawProperty("websiteUri", place.getWebsiteUri()); // Uri
		append.rawProperty("nameLanguageCode", place.getNameLanguageCode()); // String

		append.rawProperty("utcOffsetMinutes", place.getUtcOffsetMinutes()); // Integer
		append.rawProperty("openingHours", place.getOpeningHours()); // OpeningHours
		append.rawProperty("currentOpeningHours", place.getCurrentOpeningHours()); // OpeningHours
		append.rawProperty("secondaryOpeningHours", place.getSecondaryOpeningHours()); // List<OpeningHours>

		append.rawProperty("types", getTypes(place)); // List<Type>
		append.rawProperty("placeTypes", place.getPlaceTypes()); // List<String>
		append.rawProperty("businessStatus", place.getBusinessStatus()); // BusinessStatus
		append.rawProperty("editorialSummary", place.getEditorialSummary()); // String
		append.rawProperty("editorialSummaryLanguageCode", place.getEditorialSummaryLanguageCode()); // String

		append.rawProperty("attributions", place.getAttributions()); // List<String>
		append.rawProperty("rating", place.getRating()); // Double
		append.rawProperty("userRatingsTotal", place.getUserRatingsTotal()); // Integer
		append.rawProperty("priceLevel", place.getPriceLevel()); // Integer
		append.rawProperty("reviews", place.getReviews()); // List<Review>

		append.rawProperty("iconUrl", place.getIconUrl()); // String
		append.rawProperty("iconBackgroundColor", place.getIconBackgroundColor()); // Integer

		// BooleanPlaceAttributeValue
		append.rawProperty("curbsidePickup", place.getCurbsidePickup());
		append.rawProperty("delivery", place.getDelivery());
		append.rawProperty("dineIn", place.getDineIn());
		append.rawProperty("reservable", place.getReservable());
		append.rawProperty("servesBeer", place.getServesBeer());
		append.rawProperty("servesBreakfast", place.getServesBreakfast());
		append.rawProperty("servesBrunch", place.getServesBrunch());
		append.rawProperty("servesDinner", place.getServesDinner());
		append.rawProperty("servesLunch", place.getServesLunch());
		append.rawProperty("servesVegetarianFood", place.getServesVegetarianFood());
		append.rawProperty("servesWine", place.getServesWine());
		append.rawProperty("takeout", place.getTakeout());
		append.rawProperty("wheelchairAccessibleEntrance", place.getWheelchairAccessibleEntrance());

		append.rawProperty("photoMetadatas", place.getPhotoMetadatas()); // List<PhotoMetadata>
	}

	@SuppressWarnings("deprecation")
	private static @Nullable List<Place.Type> getTypes(@NonNull Place place) {
		return place.getTypes();
	}
}
