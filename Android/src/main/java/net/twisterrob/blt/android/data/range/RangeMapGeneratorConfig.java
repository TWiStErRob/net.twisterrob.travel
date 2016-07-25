package net.twisterrob.blt.android.data.range;

import static java.util.concurrent.TimeUnit.*;

@SuppressWarnings("unused")
public class RangeMapGeneratorConfig {
	private static final float MPH_TO_KPH = 1.609344f;
	/** Average male jogging speed */
	public static final float WALK_JOG_MALE = 8.3f * MPH_TO_KPH;
	public static final float WALK_JOG_FEMALE = 6.5f * MPH_TO_KPH;
	public static final float WALK_SPRINT = 15.9f * MPH_TO_KPH;
	public static final float WALK_HEALTHY = 3f * MPH_TO_KPH;
	public static final float WALK_OLIMPIC = 9.6f * MPH_TO_KPH;
	/** Usain Bolt's London 2012 world record {@code 1 / (run_time * (1km/100m) / (minutes * seconds)} */
	public static final float WALK_BOLT = 1 / (9.58f * (1000 / 100) / (60 * 60));

	public static final float SPEED_ON_FOOT_MIN = 0 /*km/h*/;
	public static final float SPEED_ON_FOOT_MAX = WALK_BOLT /*km/h*/;
	public static final float MINUTES_MIN = MINUTES.toMinutes(0);
	public static final float MINUTES_MAX = HOURS.toMinutes(6);
	public static final float START_WALK_MIN = MINUTES.toMinutes(0);
	public static final float START_WALK_MAX = MINUTES_MAX;
	public static final float TIME_PLATFORM_TO_STREET_MIN = MINUTES.toMinutes(0);
	public static final float TIME_PLATFORM_TO_STREET_MAX = MINUTES.toMinutes(30);
	public static final float TIME_TRANSFER_MIN = MINUTES.toMinutes(0);
	public static final float TIME_TRANSFER_MAX = MINUTES.toMinutes(30);
	/** minutes */
	float intraStationInterchangeTime;
	/** minutes */
	float platformToStreetTime;
	/** km/h */
	float walkingSpeed;
	/** minutes */
	float totalAllottedTime;
	/** minutes */
	float initialAllottedWalkTime;

	//	DistanceStrategy tubingStrategy = new AverageSpeedTubingStrategy();
	DistanceStrategy tubingStrategy = new SmartTubingStrategy();

	boolean allowIntraStationInterchange = true;
	boolean allowInterStationInterchange = false;
	public RangeMapGeneratorConfig() {
		setIntraStationInterchangeTime(5);
		setPlatformToStreetTime(MINUTES.toMinutes(1));
		setWalkingSpeed(4.5f);
		setTotalAllottedTime(MINUTES.toMinutes(25));
		setInitialAllottedWalkTime(MINUTES.toMinutes(10));
	}

	public RangeMapGeneratorConfig(RangeMapGeneratorConfig config) {
		set(config);
	}

	public void set(RangeMapGeneratorConfig other) {
		this.intraStationInterchangeTime = other.intraStationInterchangeTime;
		this.platformToStreetTime = other.platformToStreetTime;
		this.walkingSpeed = other.walkingSpeed;
		this.tubingStrategy = other.tubingStrategy;
		this.totalAllottedTime = other.totalAllottedTime;
		this.allowIntraStationInterchange = other.allowIntraStationInterchange;
		this.allowInterStationInterchange = other.allowInterStationInterchange;
		this.initialAllottedWalkTime = other.initialAllottedWalkTime;
	}

	/**
	 * Average time it takes in minutes to transfer from one train to another without leaving the station.
	 * Only valid if {@link #allowIntraStationInterchange} is allowed.
	 * @see #setIntraStationInterchange(boolean)
	 * @see #TIME_TRANSFER_MIN
	 * @see #TIME_TRANSFER_MAX
	 */
	public RangeMapGeneratorConfig setIntraStationInterchangeTime(float transferTime) {
		this.intraStationInterchangeTime = transferTime;
		return this;
	}

	/**
	 * Average time it takes in minutes from train doors opening to leaving the station and being on the street.
	 * @see #TIME_PLATFORM_TO_STREET_MIN
	 * @see #TIME_PLATFORM_TO_STREET_MAX
	 */
	public RangeMapGeneratorConfig setPlatformToStreetTime(float transferTime) {
		this.platformToStreetTime = transferTime;
		return this;
	}

	/**
	 * Average walking speed in km/h for calculating distances without boarding any public transport.
	 * @see RangeMapGeneratorConfig constants starting with <code>WALK_</code>
	 * @see <a href="http://www.telegraph.co.uk/sport/olympics/athletics/9450234/100m-final-how-fast-could-you-run-it.html">
	 *     Article on running speeds</a>
	 * @see #SPEED_ON_FOOT_MIN
	 * @see #SPEED_ON_FOOT_MAX
	 */
	public RangeMapGeneratorConfig setWalkingSpeed(float speed) {
		this.walkingSpeed = speed;
		return this;
	}

	/**
	 * Only the distance between tube stations on a particular line is known,
	 * this is used to calculate how long it takes for a train to go between the two stations. 
	 */
	public RangeMapGeneratorConfig setTubingStrategy(DistanceStrategy distance) {
		this.tubingStrategy = distance;
		return this;
	}

	/**
	 * Allowed time in minutes for the travel to take from the starting point.
	 * @see #MINUTES_MIN
	 * @see #MINUTES_MAX
	 */
	public RangeMapGeneratorConfig setTotalAllottedTime(float minutes) {
		this.totalAllottedTime = minutes;
		return this;
	}

	/**
	 * Allow transfers without leaving the station. {@link #intraStationInterchangeTime} determines how long it takes to transfer.
	 * @see #setIntraStationInterchangeTime(float)
	 */
	public RangeMapGeneratorConfig setIntraStationInterchange(boolean allow) {
		this.allowIntraStationInterchange = allow;
		return this;
	}

	/**
	 * Allow transfers by leaving the station and walking to another one nearby.
	 * Time to leave the station and time to enter the station will be accounted via {@link #platformToStreetTime}.
	 * @see #setPlatformToStreetTime(float)
	 * @see #START_WALK_MIN
	 * @see #START_WALK_MAX
	 */
	public RangeMapGeneratorConfig setInterStationInterchange(boolean allow) {
		this.allowInterStationInterchange = allow;
		return this;
	}

	/**
	 * Maximum amount of time in minutes to reach the first station to board a train by walking.
	 * Think about it like going from home to the nearest station.
	 * @see #START_WALK_MIN
	 * @see #START_WALK_MAX
	 */
	public RangeMapGeneratorConfig setInitialAllottedWalkTime(float transferTime) {
		this.initialAllottedWalkTime = transferTime;
		return this;
	}

	public float getIntraStationInterchangeTime() {
		return intraStationInterchangeTime;
	}
	public float getPlatformToStreetTime() {
		return platformToStreetTime;
	}
	public float getWalkingSpeed() {
		return walkingSpeed;
	}
	public float getTotalAllottedTime() {
		return totalAllottedTime;
	}
	public float getInitialAllottedWalkTime() {
		return initialAllottedWalkTime;
	}
	public DistanceStrategy getTubingStrategy() {
		return tubingStrategy;
	}
	public boolean allowsIntraStationInterchange() {
		return allowIntraStationInterchange;
	}
	public boolean allowsInterStationInterchange() {
		return allowInterStationInterchange;
	}

	/**
	 * @param meters how far to walk
	 * @return how long it takes to walk that far
	 */
	public double walk(double meters) {
		return meters
				/ 1000.0 // to kilometers
				/ walkingSpeed // to hours
				* 60 // to minutes
				;
	}
}
