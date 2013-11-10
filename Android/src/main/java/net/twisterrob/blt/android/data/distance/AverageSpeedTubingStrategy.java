package net.twisterrob.blt.android.data.distance;

import net.twisterrob.blt.android.db.model.NetworkLink;

public class AverageSpeedTubingStrategy implements DistanceStrategy {
	private static final double TIME_AT_STATION_AVG = 1.3;
	private static final double TIME_TRANSFER_AVG = 4;
	private static final double SPEED_TUBE_AVG = 33 /* km/h */;

	public double distance(NetworkLink from, NetworkLink to) {
		double penaltyAtStation = from.getLine() == to.getLine()? TIME_AT_STATION_AVG : TIME_TRANSFER_AVG;
		return to.getDistance() / 1000.0 /* to km */* SPEED_TUBE_AVG / 60.0 /* to minutes */+ penaltyAtStation;
	}
}
