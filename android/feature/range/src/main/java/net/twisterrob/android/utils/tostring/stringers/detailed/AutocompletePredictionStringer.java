package net.twisterrob.android.utils.tostring.stringers.detailed;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import net.twisterrob.java.utils.tostring.Stringer;
import net.twisterrob.java.utils.tostring.ToStringAppender;

public class AutocompletePredictionStringer extends Stringer<AutocompletePrediction> {

	@Override
	public void toString(@NonNull ToStringAppender append, AutocompletePrediction prediction) {
		append.identity(prediction.getPlaceId(), null);

		append.rawProperty("distanceMeters", prediction.getDistanceMeters());
		append.rawProperty("fullText", prediction.getFullText(null));
		append.rawProperty("primaryText", prediction.getPrimaryText(null));
		append.rawProperty("secondaryText", prediction.getSecondaryText(null));
		append.rawProperty("placeTypes", prediction.getTypes());
	}
}
