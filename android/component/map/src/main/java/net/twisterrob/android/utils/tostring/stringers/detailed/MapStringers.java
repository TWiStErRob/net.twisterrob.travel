package net.twisterrob.android.utils.tostring.stringers.detailed;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import net.twisterrob.java.utils.tostring.StringerRepo;

public class MapStringers {

	public static void register(StringerRepo repo) {
		repo.register(Status.class, new StatusStringer());
		repo.register(LatLng.class, new LatLngStringer());
		repo.register(LatLngBounds.class, new LatLngBoundsStringer());
	}
}
