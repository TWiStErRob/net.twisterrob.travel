package net.twisterrob.android.utils.tostring.stringers.detailed;

import com.google.android.libraries.places.api.model.Place;

import net.twisterrob.java.utils.tostring.StringerRepo;

public class RangeStringers {

	public static void register(StringerRepo repo) {
		MapStringers.register(repo);
		repo.register(Place.class, new PlaceStringer());
	}
}
