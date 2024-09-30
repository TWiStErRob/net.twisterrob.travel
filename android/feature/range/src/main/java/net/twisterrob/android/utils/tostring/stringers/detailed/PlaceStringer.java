package net.twisterrob.android.utils.tostring.stringers.detailed;

import java.util.List;

import android.annotation.SuppressLint;

import com.google.android.libraries.places.api.model.AccessibilityOptions;
import com.google.android.libraries.places.api.model.Place;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class PlaceStringer extends Stringer<Place> {

	@SuppressLint("VisibleForTests")
	@Override public void toString(@NonNull ToStringAppender append, Place place) {
		append.identity(place.getId(), place.getDisplayName());

		append.rawProperty("location", place.getLocation()); // LatLng
		append.rawProperty("formattedAddress", place.getFormattedAddress()); // String
		append.rawProperty("addressComponents", place.getAddressComponents()); // AddressComponents
		append.rawProperty("viewport", place.getViewport()); // LatLngBounds
		append.rawProperty("plusCode", place.getPlusCode()); // PlusCode

		append.rawProperty("internationalPhoneNumber", place.getInternationalPhoneNumber()); // String
		append.rawProperty("websiteUri", place.getWebsiteUri()); // Uri
		append.rawProperty("displayNameLanguageCode", place.getDisplayNameLanguageCode()); // String

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
		append.rawProperty("userRatingCount", place.getUserRatingCount()); // Integer
		append.rawProperty("priceLevel", place.getPriceLevel()); // Integer
		append.rawProperty("reviews", place.getReviews()); // List<Review>

		append.rawProperty("iconMaskUrl", place.getIconMaskUrl()); // String
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

		AccessibilityOptions accessibilityOptions = place.getAccessibilityOptions();
		if (accessibilityOptions != null) {
			append.beginPropertyGroup("accessibilityOptions.wheelchairAccessible");
			append.rawProperty("entrance", accessibilityOptions.getWheelchairAccessibleEntrance());
			append.rawProperty("parking", accessibilityOptions.getWheelchairAccessibleParking());
			append.rawProperty("restroom", accessibilityOptions.getWheelchairAccessibleRestroom());
			append.rawProperty("seating", accessibilityOptions.getWheelchairAccessibleSeating());
			append.endPropertyGroup();
		}

		append.rawProperty("photoMetadatas", place.getPhotoMetadatas()); // List<PhotoMetadata>
	}

	@SuppressWarnings("deprecation")
	private static @Nullable List<Place.Type> getTypes(@NonNull Place place) {
		return place.getTypes();
	}
}
