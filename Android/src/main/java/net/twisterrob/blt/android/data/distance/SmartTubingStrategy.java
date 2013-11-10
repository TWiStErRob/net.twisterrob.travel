package net.twisterrob.blt.android.data.distance;

import net.twisterrob.blt.android.db.model.NetworkLink;

public class SmartTubingStrategy implements DistanceStrategy {
	private static final int DISTANCE_SPEED_UP = 300;
	private static final int DISTANCE_SLOW_DOWN = 300;
	private static final double TIME_SPEED_UP = 0.70;
	private static final double TIME_SLOW_DOWN = 0.70;
	private static final double TIME_AT_STATION = 1.0;
	private static final double SPEED_TUBE = 70 /* km/h */;

	public double distance(NetworkLink link) {
		double penalty = TIME_SLOW_DOWN + TIME_AT_STATION + TIME_SPEED_UP;
		int travellingDistance = link.getDistance() - DISTANCE_SPEED_UP - DISTANCE_SLOW_DOWN;
		return travellingDistance / 1000.0 /* to km */* SPEED_TUBE / 60.0 /* to minutes */+ penalty;
	}
}
