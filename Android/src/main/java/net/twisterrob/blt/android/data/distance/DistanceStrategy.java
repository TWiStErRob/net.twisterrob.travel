package net.twisterrob.blt.android.data.distance;

import net.twisterrob.blt.android.db.model.NetworkLink;

public interface DistanceStrategy {
	double distance(NetworkLink from, NetworkLink to);
}
