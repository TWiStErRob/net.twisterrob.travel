package net.twisterrob.blt.android.data.distance;

import net.twisterrob.blt.android.db.model.*;

public interface DistanceStrategy {
	double distance(NetworkNode node, NetworkLink link);
}
