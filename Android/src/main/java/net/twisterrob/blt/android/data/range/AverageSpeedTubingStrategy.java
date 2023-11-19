package net.twisterrob.blt.android.data.range;

import net.twisterrob.blt.android.db.model.NetworkLink;

public class AverageSpeedTubingStrategy implements DistanceStrategy {
	private static final double TIME_AT_STATION_AVG = 1.3;
	private static final double SPEED_TUBE_AVG = 33 /* km/h */;

	public double distance(NetworkLink link) {
		return link.getDistance() / 1000.0 /* to km */ * SPEED_TUBE_AVG / 60.0 /* to minutes */ + TIME_AT_STATION_AVG;
	}
}
