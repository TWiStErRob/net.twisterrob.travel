package com.twister.london.travel.io.feeds;
import static com.twister.london.travel.io.feeds.Feed.Const.*;
import static com.twister.london.travel.io.feeds.Feed.Type.*;

import java.net.*;
public enum Feed {

	/**
	 * Step-Free Tube Guide Data <br>
	 * Spec: http://www.tfl.gov.uk/assets/downloads/businessandpartners/SFTG_Specification.pdf <br>
	 * Schema: http://www.tfl.gov.uk/assets/downloads/businessandpartners/SFTG_Schema.xsd <br>
	 */
	StepFreeTubeGuide(YEAR / 4, N_A, N_A, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/SFTG_Data.xml",
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/SFTG_Data.xml"),

	/**
	 * Journey Planner API <br>
	 * Example: http://jpapi.tfl.gov.uk<br>
	 * The JP API is IP locked. When we’ve reviewed your registration information we’ll send you an email to let you
	 * know if access is granted to the IP address you have provided.
	 * Spec: http://www.tfl.gov.uk/assets/downloads/businessandpartners/journey-planner-api-documentation.pdf.pdf
	 */
	JourneyPlanner(N_A, N_A, N_A, MISSING_HANDLER, MISSING_SAMPLE, "http://jpapi.tfl.gov.uk"),

	/**
	 * Live Traffic Disruptions (TIMS) <br>
	 * Spec: http://www.tfl.gov.uk/assets/downloads/businessandpartners/TIMS_Feed_Technical_Specification_-_010313.PDF
	 * Sample: http://www.tfl.gov.uk/assets/downloads/businessandpartners/tims_feed_sample.xml
	 * Sample: http://www.tfl.gov.uk/assets/downloads/businessandpartners/tims_feed_error.xml
	 * Schema: http://www.tfl.gov.uk/assets/downloads/businessandpartners/s.xsd
	 * Example: http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=13
	 */
	LiveTrafficDisruptions(13, 5 * MINUTE, 5 * MINUTE, 30 * MINUTE, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Live bus and river bus arrivals API (instant) <br>
	 * Example: http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1
	 * Spec: http://www.tfl.gov.uk/assets/downloads/businessandpartners/tfl-live-bus-and-river-bus-arrivals-api-documentation.pdf
	 */
	LiveBusArrivals(30 * SECOND, 5 * SECOND, 30 * SECOND, MISSING_HANDLER,
			"http://countdown.api.tfl.gov.uk/interfaces/ura/instant",
			"http://countdown.api.tfl.gov.uk/interfaces/ura/instant"),

	/**
	 * Live bus and river bus arrivals API (stream)
	 * Spec: http://www.tfl.gov.uk/assets/downloads/businessandpartners/tfl-live-bus-and-river-bus-arrivals-api-documentation.pdf
	 */
	StreamBusArrivals(30 * SECOND, 5 * SECOND, 30 * SECOND, MISSING_HANDLER,
			"http://countdown.api.tfl.gov.uk/interfaces/ura/stream",
			"http://countdown.api.tfl.gov.uk/interfaces/ura/stream"),

	/**
	 * Source London charge points and data dictionary feed
	 */
	SourceLondonChargePoints(1 * WEEK, 1 * DAY, 1 * WEEK, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/charge-point-locations.js", MISSING_URL),
	/**
	 * Source London charge points and data dictionary feed
	 */
	SourceLondonChargePointsDictionary(1 * WEEK, 1 * DAY, 1 * WEEK, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/charge-point-data-dictionary-sample.js",
			MISSING_URL),

	/**
	 * Tube departure boards, line and station status <br>
	 * Example: http://cloud.tfl.gov.uk/TrackerNet/LineStatus
	 */
	TubeDepartureBoardsLineStatus(MISSING_TIME, MISSING_TIME, MISSING_TIME, LineStatusFeedHandler.class,
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus", "http://cloud.tfl.gov.uk/TrackerNet/LineStatus"),
	TubeDepartureBoardsLineStatusIncidents(MISSING_TIME, MISSING_TIME, MISSING_TIME, LineStatusFeedHandler.class,
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus/IncidentsOnly",
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus/IncidentsOnly"),

	/**
	 * Barclays Cycle Hire statistics <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=21
	 */
	BarclayCycleHireStatistics(21, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Barclays Cycle Hire availability <br>
	 * Example: http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml
	 */
	BarclaysCycleHireAvailablility(MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER,
			"http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml",
			"http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml"),

	/**
	 * Oyster card journey information <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=19
	 */
	OysterCardJourneyInfo(19, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Journey Planner Timetables <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=15
	 */
	JourneyPlannerTimetables(15, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Live Roadside Message Signs v2 <br>
	 * Example: http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=33
	 */
	LiveRoadSideMessageSignsV2(33, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * 
	 */
	CoachParkingLocations(MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE, MISSING_URL),

	/**
	 * 
	 */
	DialARideStatistics(MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE, MISSING_URL),

	/**
	 * 
	 */
	LiveTrafficCameras(MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE, MISSING_URL),

	/**
	 * 
	 */
	FindARide(MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE, MISSING_URL),

	/**
	 * Station locations <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=4
	 */
	StationLocations(4, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Pier locations <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=8
	 */
	PierLocations(8, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Oyster Ticket Stop locations <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=9
	 */
	OysterLocations(9, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Bus stop locations <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=10
	 */
	BusStopLocations(10, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Bus routes <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=11
	 */
	BusRoutes(11, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Tube - this weekend <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=1
	 */
	TubeThisWeekend(1, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),
	/**
	 * Tube - this weekend v2 <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=7
	 */
	TubeThisWeekendV2(7, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Public transport accessibility levels <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=23
	 */
	AccessibilityLevels(23, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Rolling origin and destination survey <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=24
	 */
	RollingOnDSurvey(24, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Station facilities <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=16
	 */
	StationFacilities(16, MISSING_TIME, MISSING_TIME, MISSING_TIME, FacilitiesFeedHandler.class, MISSING_SAMPLE),

	/**
	 * London Underground passenger counts <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=25
	 */
	PassengerCounts(25, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Tube station accessibility data <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=26
	 */
	TubeStationAccessibility(26, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * Games Postcodes <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=29
	 */
	GamesPostcodes(29, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * River services timetables <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=22
	 */
	RiverTimetables(22, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE)

	;

	/**
	 * @see Feed.Type
	 */
	private final Type m_type;

	/**
	 * How often we publish a fresh copy of the feed
	 * From http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx
	 */
	private int m_freshTime;

	/**
	 * Maximum time allowed between capturing and displaying the feed
	 * From http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx
	 */
	private int m_maxDelay;

	/**
	 * Maximum time information can be displayed before being updated
	 * From http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx
	 */
	private int m_maxDisplay;
	private URL m_url = null;
	private Class<? extends BaseFeedHandler<? extends BaseFeed>> m_handler;
	private final URL m_exampleUrl;

	/**
	 * Only for {@link Type#Syndication} urls.
	 */
	private int m_feedId = -1;

	private Feed(Type type, int freshTime, int maxDelay, int maxDisplay,
			Class<? extends BaseFeedHandler<? extends BaseFeed>> handler, String exampleUrl) {
		this.m_type = type;
		this.m_freshTime = freshTime;
		this.m_maxDelay = maxDelay;
		this.m_maxDisplay = maxDisplay;
		this.m_handler = handler;
		this.m_exampleUrl = exampleUrl == null? null : createURL(exampleUrl);
	}

	private Feed(int freshTime, int maxDelay, int maxDisplay,
			Class<? extends BaseFeedHandler<? extends BaseFeed>> handler, String exampleUrl, String url) {
		this(Other, freshTime, maxDelay, maxDisplay, handler, exampleUrl);
		m_url = url != null? createURL(url) : null;
	}
	private Feed(int feedId, int freshTime, int maxDelay, int maxDisplay,
			Class<? extends BaseFeedHandler<? extends BaseFeed>> handler, String exampleUrl) {
		this(Syndication, freshTime, maxDelay, maxDisplay, handler, exampleUrl);
		m_feedId = feedId;
	}

	public Type getType() {
		return m_type;
	}
	public int getFreshTime() {
		return m_freshTime;
	}
	public int getMaxDelay() {
		return m_maxDelay;
	}
	public int getMaxDisplay() {
		return m_maxDisplay;
	}
	public int getFeedId() {
		return m_feedId;
	}
	public URL getUrl() {
		return m_url != null? m_url : m_type.getBaseUrl();
	}

	/**
	 * Useless in production.
	 */
	public URL getExampleUrl() {
		return m_exampleUrl;
	}

	private static URL createURL(String spec) {
		try {
			return new URL(spec);
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}
	public static enum Type {
		Syndication("http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx"),
		Other(null);
		private final URL m_baseUrl;
		private Type(String baseUrl) {
			m_baseUrl = baseUrl != null? createURL(baseUrl) : null;
		}
		public URL getBaseUrl() {
			return m_baseUrl;
		}
	}
	public BaseFeedHandler<? extends BaseFeed> getHandler() {
		try {
			return m_handler.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected static interface Const {
		int SECOND = 1000;
		int MINUTE = 60 * SECOND;
		int HOUR = 60 * MINUTE;
		int DAY = 24 * HOUR;
		int WEEK = 7 * DAY;
		int YEAR = 365 * DAY;
		int MISSING_TIME = 0;
		int N_A = 0;
		String MISSING_URL = null;
		String MISSING_SAMPLE = null;
		Class<? extends BaseFeedHandler<? extends BaseFeed>> MISSING_HANDLER = null;
	}
}
