package com.twister.london.travel.io.feeds;
import static com.twister.london.travel.io.feeds.Feed.Type.*;

import java.net.*;
public enum Feed {

	/**
	 * Live bus arrivals API (instant) <br>
	 * Example: http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1
	 */
	LiveBusArrivalsInstant(Other, null, "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1",
			"http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1"),
	/**
	 * Live Roadside Message Signs v2 <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=33
	 */
	LiveRoadSideMessageSignsV2(Syndication, null, null, 26),
	/**
	 * Games Postcodes <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=29
	 */
	GamesPostcodes(Syndication, null, null, 26),
	/**
	 * Journey Planner API Beta <br>
	 * Example: http://jpapi.tfl.gov.uk<br>
	 * The JP API is IP locked. When we’ve reviewed your registration information we’ll send you an email to let you
	 * know if access is granted to the IP address you have provided.
	 */
	JourneyPlanner(Other, null, null, "http://jpapi.tfl.gov.uk"),
	/**
	 * Barclays Cycle Hire availability <br>
	 * Example: http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml
	 */
	BarclaysCycleHire(Other, null, "http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml",
			"http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml"),
	/**
	 * Tube station accessibility data <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=26
	 */
	TubeStationAccessibility(Syndication, null, null, 26),
	/**
	 * London Underground passenger counts <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=25
	 */
	PassengerCounts(Syndication, null, null, 25),
	/**
	 * Rolling origin and destination survey <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=24
	 */
	RollingOnDSurvey(Syndication, null, null, 24),
	/**
	 * Public transport accessibility levels <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=23
	 */
	AccessibilityLevels(Syndication, null, null, 23),
	/**
	 * River services timetables <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=22
	 */
	RiverTimetables(Syndication, null, null, 22),
	/**
	 * Barclays Cycle Hire statistics <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=21
	 */
	BarclayCycleHireStatistics(Syndication, null, null, 21),
	/**
	 * Tube departure boards, line and station status <br>
	 * Example: http://cloud.tfl.gov.uk/TrackerNet/LineStatus
	 */
	TubeDepartureBoardsLineStatus(Other, LineStatusFeedHandler.class, "http://cloud.tfl.gov.uk/TrackerNet/LineStatus",
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus"),
	TubeDepartureBoardsLineStatusIncidents(Other, LineStatusFeedHandler.class,
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus/IncidentsOnly",
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus/IncidentsOnly"),
	/**
	 * Oyster card journey information <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=19
	 */
	OysterCardJourneyInfo(Syndication, null, null, 19),
	/**
	 * Journey Planner Timetables <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=15
	 */
	JourneyPlannerTimetables(Syndication, null, null, 15),
	/**
	 * Live Traffic Disruptions <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=13
	 */
	LiveTrafficDisruptions(Syndication, null, null, 13),
	/**
	 * Station locations <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=4
	 */
	StationLocations(Syndication, null, null, 4),
	/**
	 * Pier locations <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=8
	 */
	PierLocations(Syndication, null, null, 8),
	/**
	 * Oyster Ticket Stop locations <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=9
	 */
	OysterLocations(Syndication, null, null, 9),
	/**
	 * Bus routes <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=11
	 */
	BusRoutes(Syndication, null, null, 11),
	/**
	 * Bus stop locations <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=10
	 */
	BusStopLocations(Syndication, null, null, 10),
	/**
	 * Tube - this weekend <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=1
	 */
	TubeThisWeekend(Syndication, null, null, 1),
	/**
	 * Tube - this weekend v2 <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=7
	 */
	TubeThisWeekendV2(Syndication, null, null, 7),
	/**
	 * Station facilities <br>
	 * Example:
	 * http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=16
	 */
	StationFacilities(Syndication, FacilitiesFeedHandler.class, null, 16);

	private final Type m_type;
	private URL m_url = null;
	private Class<? extends BaseFeedHandler<? extends BaseFeed>> m_handler;
	private final URL m_exampleUrl;

	/**
	 * Only for {@link Type#Syndication} urls.
	 */
	private int m_feedId = -1;

	private Feed(Type type, Class<? extends BaseFeedHandler<? extends BaseFeed>> handler, String exampleUrl, String url) {
		m_type = type;
		m_handler = handler;
		m_url = createURL(url != null? url : type.getBaseUrl().toString());
		m_exampleUrl = exampleUrl == null? null : createURL(exampleUrl);
	}

	private Feed(Type type, Class<? extends BaseFeedHandler<? extends BaseFeed>> handler, String exampleUrl, int feedId) {
		this(type, handler, exampleUrl, null);
		if (type != Syndication) {
			throw new UnsupportedOperationException("Feed ID is only for " + Type.Syndication + " feeds");
		}
		m_feedId = feedId;
	}

	public Type getType() {
		return m_type;
	}
	public int getFeedId() {
		return m_feedId;
	}
	public URL getUrl() {
		return m_url;
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
}
