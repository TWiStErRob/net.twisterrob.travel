package net.twisterrob.blt.android.data.distance;

import android.support.annotation.FloatRange;

public class DistanceMapGeneratorConfig {
	private static final double MPH_TO_KPH = 1.609344;
	private static final int HOURS_IN_MINUTES = 60;
	/** Average male jogging speed */
	public static final double WALK_JOG_MALE = 8.3 * MPH_TO_KPH;
	public static final double WALK_JOG_FEMALE = 6.5 * MPH_TO_KPH;
	public static final double WALK_SPRINT = 15.9 * MPH_TO_KPH;
	public static final double WALK_HEALTHY = 3 * MPH_TO_KPH;
	public static final double WALK_OLIMPIC = 9.6 * MPH_TO_KPH;
	/** Usain Bolt's London 2012 world record {@code 1 / (run_time * (1km/100m) / (minutes * seconds)} */
	public static final double WALK_BOLT = 1 / (9.58 * (1000 / 100) / (60 * 60));
	/** minutes */
	double timeTransfer = 5;
	/** minutes */
	double timePlatformToStreet = 1;
	/** km/h */
	double speedOnFoot;
	/** minutes */
	double minutes;
	/** minutes */
	double startWalkMinutes;

	DistanceStrategy tubingStrategy = new AverageSpeedTubingStrategy();
	//DistanceStrategy tubingStrategy = new SmartTubingStrategy();

	boolean transferInStation = true;
	boolean transferWalk = false;
	public DistanceMapGeneratorConfig() {
		timeTransfer(5);
		timePlatformToStreet(1);
		speedOnFoot(4.5);
		startWalkMinutes(5);
	}
	public DistanceMapGeneratorConfig(DistanceMapGeneratorConfig config) {
		timeTransfer(config.timeTransfer);
		timePlatformToStreet(config.timePlatformToStreet);
		speedOnFoot(config.speedOnFoot);
		tubingStrategy(config.tubingStrategy);
		minutes(config.minutes);
		transferInStation(config.transferInStation);
		transferWalk(config.transferWalk);
		startWalkMinutes(config.startWalkMinutes);
	}

	/**
	 * Average time it takes in minutes to transfer from one train to another without leaving the station.
	 * Only valid if {@link #transferInStation} is allowed.
	 * @see #transferInStation(boolean)
	 */
	public DistanceMapGeneratorConfig timeTransfer(double timeTransfer) {
		this.timeTransfer = timeTransfer;
		return this;
	}

	/**
	 * Average time it takes in minutes from train doors opening to leaving the station and being on the street.
	 */
	public DistanceMapGeneratorConfig timePlatformToStreet(
			@FloatRange(from = 0, to = 30 /*minutes*/) double platformToStreet) {
		this.timePlatformToStreet = platformToStreet;
		return this;
	}

	/**
	 * Average walking speed in km/h for calculating distances without boarding any public transport.
	 * @see DistanceMapGeneratorConfig constants starting with <code>WALK_</code>
	 * @see <a href="http://www.telegraph.co.uk/sport/olympics/athletics/9450234/100m-final-how-fast-could-you-run-it.html">
	 *     Article on running speeds</a>
	 */
	public DistanceMapGeneratorConfig speedOnFoot(
			@FloatRange(from = 0, to = WALK_BOLT) double speed) {
		this.speedOnFoot = speed;
		return this;
	}

	/**
	 * Only the distance between tube stations on a particular line is known,
	 * this is used to calculate how long it takes for a train to go between the two stations. 
	 */
	public DistanceMapGeneratorConfig tubingStrategy(DistanceStrategy distance) {
		this.tubingStrategy = distance;
		return this;
	}

	/**
	 * Allowed time in minutes for the travel to take from the starting point.
	 */
	public DistanceMapGeneratorConfig minutes(
			@FloatRange(from = 0, to = 6 * HOURS_IN_MINUTES, fromInclusive = false) double minutes) {
		this.minutes = minutes;
		return this;
	}

	/**
	 * Allow transfers without leaving the station. {@link #timeTransfer} determines how long it takes to transfer.
	 * @see #timeTransfer(double)
	 */
	public DistanceMapGeneratorConfig transferInStation(boolean transferInStation) {
		this.transferInStation = transferInStation;
		return this;
	}

	/**
	 * Allow transfers by leaving the station and walking to another one nearby.
	 * Time to leave the station and time to enter the station will be accounted via {@link #timePlatformToStreet}.
	 * @see #timePlatformToStreet(double)
	 */
	public DistanceMapGeneratorConfig transferWalk(boolean transferWalk) {
		this.transferWalk = transferWalk;
		return this;
	}

	/**
	 * Maximum amount of time in minutes to reach the first station to board a train by walking.
	 * Think about it like going from home to the nearest station.
	 */
	public DistanceMapGeneratorConfig startWalkMinutes(double startWalkMinutes) {
		this.startWalkMinutes = startWalkMinutes;
		return this;
	}
}
