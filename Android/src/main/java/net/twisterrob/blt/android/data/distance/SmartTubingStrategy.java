package net.twisterrob.blt.android.data.distance;

import net.twisterrob.blt.android.db.model.*;

public class SmartTubingStrategy implements DistanceStrategy {
	private static final int DISTANCE_SPEED_UP = 300;
	private static final int DISTANCE_SLOW_DOWN = 300;
	private static final double TIME_SPEED_UP = 0.70;
	private static final double TIME_SLOW_DOWN = 0.70;
	private static final double TIME_AT_STATION = 1.0;
	private static final double SPEED_TUBE = 70 /* km/h */;

	public double distance(NetworkNode node, NetworkLink link) {
		return (link.m_distance - DISTANCE_SPEED_UP - DISTANCE_SLOW_DOWN) / 1000.0 /* to km */
				* SPEED_TUBE / 60.0 /* to minutes */+ TIME_SPEED_UP + TIME_SLOW_DOWN + TIME_AT_STATION;
	}
}
