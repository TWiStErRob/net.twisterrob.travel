package net.twisterrob.blt.android.data.range;

import net.twisterrob.blt.android.db.model.NetworkLink;

public interface DistanceStrategy {
	double distance(NetworkLink link);
}
