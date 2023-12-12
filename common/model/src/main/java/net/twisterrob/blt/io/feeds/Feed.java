package net.twisterrob.blt.io.feeds;

import java.lang.reflect.InvocationTargetException;
import java.net.*;

import net.twisterrob.blt.io.feeds.facilities.FacilitiesFeedHandler;
import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableHandler;
import net.twisterrob.blt.io.feeds.trackernet.*;

import static net.twisterrob.blt.io.feeds.Feed.Const.*;
import static net.twisterrob.blt.io.feeds.Feed.Type.*;

/**
 * Feeds provided by Transport for London<br>
 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx">link</a><br>
 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/syndication-developer-guidelines.pdf">link</a><br>
 */
@SuppressWarnings("PointlessArithmeticExpression")
public enum Feed {
	/**
	 * <h3>Step-Free Tube Guide Data</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#28393">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/SFTG_Specification.pdf">link</a><br>
	 * <b>Schema</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/SFTG_Schema.xsd">link</a><br>
	 */
	StepFreeTubeGuide(YEAR / 4, N_A, N_A, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/SFTG_Data.xml",
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/SFTG_Data.xml"),

	/**
	 * <h3>Journey Planner API</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#20699">link</a><br>
	 * <b>Example</b>: <a href="http://jpapi.tfl.gov.uk<br>">link</a><br>
	 * The JP API is IP locked. When we’ve reviewed your registration information we’ll send you an email to let you
	 * know if access is granted to the IP address you have provided.
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/journey-planner-api-documentation.pdf.pdf">link</a><br>
	 */
	JourneyPlanner(N_A, N_A, N_A, MISSING_HANDLER, MISSING_SAMPLE, "http://jpapi.tfl.gov.uk"),

	/**
	 * <h3>Live Traffic Disruptions (TIMS)</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#27334">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/TIMS_Feed_Technical_Specification_-_010313.PDF">link</a><br>
	 * <b>Sample</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/tims_feed_sample.xml">link</a><br>
	 * <b>Sample</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/tims_feed_error.xml">link</a><br>
	 * <b>Schema</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/s.xsd">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=13">link</a><br>
	 */
	LiveTrafficDisruptions(13, 5 * MINUTE, 5 * MINUTE, 30 * MINUTE, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * <h3>Live bus and river bus arrivals API (instant)</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#21642">link</a><br>
	 * <b>Example</b>: <a href="http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/tfl-live-bus-and-river-bus-arrivals-api-documentation.pdf">link</a><br>
	 */
	LiveBusArrivals(30 * SECOND, 5 * SECOND, 30 * SECOND, MISSING_HANDLER,
			"http://countdown.api.tfl.gov.uk/interfaces/ura/instant",
			"http://countdown.api.tfl.gov.uk/interfaces/ura/instant"),
	/**
	 * <h3>Live bus and river bus arrivals API (stream)</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#23665">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/tfl-live-bus-and-river-bus-arrivals-api-documentation.pdf">link</a><br>
	 */
	StreamBusArrivals(30 * SECOND, 5 * SECOND, 30 * SECOND, MISSING_HANDLER,
			"http://countdown.api.tfl.gov.uk/interfaces/ura/stream",
			"http://countdown.api.tfl.gov.uk/interfaces/ura/stream"),

	/**
	 * <h3>Source London charge points feed</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#20396">link</a><br>
	 * @see Feed#SourceLondonChargePointsDictionary
	 */
	SourceLondonChargePoints(1 * WEEK, 1 * DAY, 1 * WEEK, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/charge-point-locations.js", MISSING_URL),
	/**
	 * <h3>Source London data dictionary feed</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#20396">link</a><br>
	 * @see Feed#SourceLondonChargePoints
	 */
	SourceLondonChargePointsDictionary(1 * WEEK, 1 * DAY, 1 * WEEK, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/charge-point-data-dictionary-sample.js",
			MISSING_URL),

	/**
	 * <h3>Tube departure boards, line summary</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17615">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">link</a> @ 3.1<br>
	 * For example reasons see:
	 * <a href="http://web.archive.org/web/20110615000000&#42;/http://www.tfl.gov.uk/tfl/livetravelnews/realtime/tube/default.html">The Wayback Machine</a><br>
	 */
	TubeDepartureBoardsPredictionSummary(30 * SECOND, 30 * SECOND, 30 * SECOND, PredictionSummaryFeedHandler.class,
			"http://cloud.tfl.gov.uk/TrackerNet/PredictionSummary/V",
			"http://cloud.tfl.gov.uk/TrackerNet/PredictionSummary/"),
	/**
	 * <h3>Tube departure boards, station details</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17615">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">link</a> @ 3.2<br>
	 */
	TubeDepartureBoardsPredictionDetailed(30 * SECOND, 30 * SECOND, 30 * SECOND, PredictionDetailedFeedHandler.class,
			"http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/C/BNK",
			"http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/"),

	/**
	 * <h3>Tube departure boards, line and station status</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17615">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">link</a> @ 3.3<br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf">link</a><br>
	 */
	TubeDepartureBoardsStationStatus(30 * SECOND, 30 * SECOND, 30 * SECOND, MISSING_HANDLER,
			"http://cloud.tfl.gov.uk/TrackerNet/StationStatus", "http://cloud.tfl.gov.uk/TrackerNet/StationStatus"),
	/**
	 * <h3>Tube departure boards, line and station status</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17615">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">link</a> @ 3.3<br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf">link</a><br>
	 * For example reasons see:
	 * <a href="http://web.archive.org/web/20110615000000&#42;/http://www.tfl.gov.uk/tfl/livetravelnews/realtime/tube/default.html">The Wayback Machine</a><br>
	 */
	TubeDepartureBoardsStationStatusIncidents(30 * SECOND, 30 * SECOND, 30 * SECOND, MISSING_HANDLER,
			"http://cloud.tfl.gov.uk/TrackerNet/StationStatus/IncidentsOnly",
			"http://cloud.tfl.gov.uk/TrackerNet/StationStatus/IncidentsOnly"),

	/**
	 * <h3>Tube departure boards, line and station status</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17615">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf">link</a> @ 3.4<br>
	 */
	TubeDepartureBoardsLineStatus(30 * SECOND, 30 * SECOND, 30 * SECOND, LineStatusFeedHandler.class,
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus", "http://cloud.tfl.gov.uk/TrackerNet/LineStatus"),
	/**
	 * <h3>Tube departure boards, line and station status</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17615">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf">link</a> @ 3.4<br>
	 * For example reasons see:
	 * <a href="http://web.archive.org/web/20110615000000&#42;/http://www.tfl.gov.uk/tfl/livetravelnews/realtime/tube/default.html">The Wayback Machine</a><br>
	 */
	TubeDepartureBoardsLineStatusIncidents(30 * SECOND, 30 * SECOND, 30 * SECOND, LineStatusFeedHandler.class,
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus/IncidentsOnly",
			"http://cloud.tfl.gov.uk/TrackerNet/LineStatus/IncidentsOnly"),
	/**
	 * <h3>Barclays Cycle Hire statistics</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17855">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=21">link</a><br>
	 */
	BarclayCycleHireStatistics(21, N_A, N_A, N_A, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/BarclaysCycleHireUsageStats.csv"),

	/**
	 * <h3>Barclays Cycle Hire availability</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17292">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/BCH_feed_data_dictionary_-_may_2011.pdf">link</a><br>
	 */
	BarclaysCycleHireAvailablility(3 * MINUTE, 30 * MINUTE, 30 * MINUTE, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/BarclaysCycleHireAvailabilityexample.xml",
			"http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml"),

	/**
	 * <h3>Oyster card journey information</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17685">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=19">link</a><br>
	 */
	OysterCardJourneyInfo(19, N_A, N_A, N_A, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/OysterCardJourneyInformationexample.csv"),

	/**
	 * <h3>Journey Planner Timetables</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17289">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=15">link</a><br>
	 */
	JourneyPlannerTimetables(15, 1 * DAY/* 1440 mins */, 10 * MINUTE, 1 * DAY/* 1440 mins */,
			JourneyPlannerTimetableHandler.class,
			"http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds"
					+ "/journeyplannertimetables/journey-planner-timetables.zip"),

	/**
	 * <h3>Live Roadside Message Signs v2</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17290">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=33">link</a><br>
	 * <b>Schema</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/liveroadsidemessagesigns-v2.xsd.xsd">link</a><br>
	 */
	LiveRoadSideMessageSignsV2(33, 5 * MINUTE, 5 * MINUTE, 30 * MINUTE, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/vms-feed-sample.xml"),

	/**
	 * <h3>Coach parking locations</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17286">link</a><br>
	 */
	CoachParkingLocations(1 * DAY/* 1440 mins */, 10 * MINUTE, 3 * DAY/* 4320 mins */, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/icm-coach-parking.csv", MISSING_URL),
	/**
	 * <h3>Coach parking locations</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17286">link</a><br>
	 */
	CoachParkingLocationsTheatre(1 * DAY/* 1440 mins */, 10 * MINUTE, 3 * DAY/* 4320 mins */, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/icm-coach-parking-theatres.csv", MISSING_URL),

	/**
	 * <h3>Dial a Ride usage statistics</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17287">link</a><br>
	 */
	DialARideStatistics(YEAR / 4, N_A, N_A, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/DialaRideUsageStatsexample.csv", MISSING_URL),

	/**
	 * <h3>Live traffic camera images (CCTV)</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17294">link</a><br>
	 * <b>Schema</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/New_Jam_Cam_schema.txt">link</a><br>
	 */
	LiveTrafficCameras(2 * MINUTE, 2 * MINUTE, 15 * MINUTE, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/cameraList.xml", MISSING_URL),

	/**
	 * <h3>Findaride</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17295">link</a><br>
	 * <b>Schema</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/findaride/FindARide_v2.xsd">link</a><br>
	 * <b>Viewer</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/findaride/FindARide_v1.xslt">link</a><br>
	 */
	FindARide(1 * DAY/* 1440 mins */, 10 * MINUTE, 3 * DAY/* 4320 mins */, MISSING_HANDLER,
			"http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/findaride/FindARide_v1.xml",
			MISSING_URL),

	/**
	 * <h3>Station locations</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17297">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=4">link</a><br>
	 * <b>Schema</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/stationlocations/StationLocations_v1.xsd">link</a><br>
	 */
	StationLocations(4, 1 * DAY/* 1440 mins */, 10 * MINUTE, 3 * DAY/* 4320 mins */, MISSING_HANDLER,
			"http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/stationlocations/Stations.kml"),

	/**
	 * <h3>Pier locations</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17296">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=8">link</a><br>
	 * <b>Schema</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/pierlocations/PierLocations_v1.xsd">link</a><br>
	 */
	PierLocations(8, 1 * DAY/* 1440 mins */, 10 * MINUTE, 3 * DAY/* 4320 mins */, MISSING_HANDLER,
			"http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/pierlocations/PierLocations_v1.kml"),

	/**
	 * <h3>Oyster Ticket Stop locations</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17293">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=9">link</a><br>
	 * <b>Schema</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/oysterticketstoplocations/oyster-stop-locations-v1.xsd">link</a><br>
	 */
	OysterLocations(9, 1 * DAY/* 1440 mins */, 10 * MINUTE, 3 * DAY/* 4320 mins */, MISSING_HANDLER,
			"http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds"
					+ "/oysterticketstoplocations/oyster-stop-locations-v1-example.kml"),

	/**
	 * <h3><h3>Bus stop locations</h3><br></h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17463">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=10">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/syndication-developer-guidelines-bus-stop-locations-routes.pdf">link</a><br>
	 * @see Feed#BusRoutes
	 */
	BusStopLocations(10, 1 * WEEK, 1 * DAY, 2 * WEEK, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/bus_stops_example.csv"),
	/**
	 * <h3>Bus routes</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17463">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=11">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/syndication-developer-guidelines-bus-stop-locations-routes.pdf">link</a><br>
	 * @see Feed#BusStopLocations
	 */
	BusRoutes(11, 1 * WEEK, 1 * DAY, 2 * WEEK, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/stop_sequences_example.csv"),

	/**
	 * <h3>Tube - this weekend</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17298">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=1">link</a><br>
	 * <b>Spec</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/tubethisweekend/TubeThisWeekend_v1.xsd">link</a><br>
	 * <b>Viewer</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/tubethisweekend/TubeThisWeekend_v1.xslt">link</a><br>
	 */
	TubeThisWeekend(1, 12 * HOUR/* 720 mins */, 2 * MINUTE, 10 * MINUTE, MISSING_HANDLER,
			"http://www.tfl.gov.uk/tfl/businessandpartners/syndication/example-feeds/tubethisweekend/TubeThisWeekend_v1.xml"),
	/**
	 * <h3>Tube - this weekend v2</h3><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=7">link</a><br>
	 */
	TubeThisWeekendV2(7, 12 * HOUR/* 720 mins */, 2 * MINUTE, 10 * MINUTE, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * <h3>Public transport accessibility levels</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17621">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=23">link</a><br>
	 */
	AccessibilityLevels(23, 1 * YEAR, N_A, N_A, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/gl-ptals-2012-data-grid-points.zip"),

	/**
	 * <h3>Rolling Origin & Destination Survey (RODS)</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17618">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=24">link</a><br>
	 */
	RollingOnDSurvey(24, 1 * YEAR, 9 * MONTH, 1 * YEAR, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/RODS_Access_Mode_2010_sample.csv"),

	/**
	 * <h3>Station facilities</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#17699">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=16">link</a><br>
	 */
	StationFacilities(16, 1 * DAY/* 1440 mins */, 10 * MINUTE, 3 * DAY/* 4320 mins */, FacilitiesFeedHandler.class,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/StationFacilitiessample.xml"),

	/**
	 * <h3>London Underground passenger counts data</h3><br>
	 * <b>Details</b>: <a href="http://www.tfl.gov.uk/businessandpartners/syndication/16493.aspx#18112">link</a><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=25">link</a><br>
	 */
	PassengerCounts(25, 1 * YEAR, N_A, N_A, MISSING_HANDLER,
			"http://www.tfl.gov.uk/assets/downloads/businessandpartners/Counts_Entries_10_Weekday_sample.csv"),

	/**
	 * <h3>Tube station accessibility data</h3><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=26">link</a><br>
	 */
	TubeStationAccessibility(26, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * <h3>Games Postcodes</h3><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=29">link</a><br>
	 */
	GamesPostcodes(29, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE),

	/**
	 * <h3>River services timetables</h3><br>
	 * <b>Example</b>: <a href="http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=papp.robert.s@gmail.com&feedId=22">link</a><br>
	 */
	RiverTimetables(22, MISSING_TIME, MISSING_TIME, MISSING_TIME, MISSING_HANDLER, MISSING_SAMPLE);

	/**
	 * @see Feed.Type
	 */
	private final Type m_type;

	/**
	 * How often we publish a fresh copy of the feed
	 */
	private long m_freshTime;

	/**
	 * Maximum time allowed between capturing and displaying the feed
	 */
	private long m_maxDelay;

	/**
	 * Maximum time information can be displayed before being updated
	 */
	private long m_maxDisplay;
	private URL m_url = null;
	private Class<? extends FeedHandler<? extends BaseFeed<?>>> m_handler;
	private final URL m_sampleUrl;

	/**
	 * Only for {@link Type#Syndication} urls.
	 */
	private int m_feedId = -1;

	Feed(Type type, long freshTime, long maxDelay, long maxDisplay,
			Class<? extends FeedHandler<? extends BaseFeed<?>>> handler, String sampleUrl) {
		this.m_type = type;
		this.m_freshTime = freshTime;
		this.m_maxDelay = maxDelay;
		this.m_maxDisplay = maxDisplay;
		this.m_handler = handler;
		this.m_sampleUrl = sampleUrl == null? null : createURL(sampleUrl);
	}

	Feed(long freshTime, long maxDelay, long maxDisplay,
			Class<? extends FeedHandler<? extends BaseFeed<?>>> handler, String sampleUrl, String url) {
		this(Other, freshTime, maxDelay, maxDisplay, handler, sampleUrl);
		m_url = url != null? createURL(url) : null;
	}
	Feed(int feedId, long freshTime, long maxDelay, long maxDisplay,
			Class<? extends FeedHandler<? extends BaseFeed<?>>> handler, String sampleUrl) {
		this(Syndication, freshTime, maxDelay, maxDisplay, handler, sampleUrl);
		m_feedId = feedId;
	}

	public Type getType() {
		return m_type;
	}
	public long getFreshTime() {
		return m_freshTime;
	}
	public long getMaxDelay() {
		return m_maxDelay;
	}
	public long getMaxDisplay() {
		return m_maxDisplay;
	}
	public int getFeedId() {
		return m_feedId;
	}
	public URL getUrl() {
		return m_url != null? m_url : m_type.getBaseUrl();
	}

	/**
	 * Useless in production
	 */
	public URL getSampleUrl() {
		return m_sampleUrl;
	}

	private static URL createURL(String spec) {
		try {
			return new URL(spec);
		} catch (MalformedURLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public enum Type {
		Syndication("http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx"),
		Other(null);

		private final URL m_baseUrl;

		Type(String baseUrl) {
			m_baseUrl = baseUrl != null? createURL(baseUrl) : null;
		}

		public URL getBaseUrl() {
			return m_baseUrl;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends BaseFeed<T>> FeedHandler<T> getHandler() {
		try {
			if (m_handler == null) {
				throw new IllegalArgumentException(this + " does not have a handler registered");
			}
			return (FeedHandler<T>)m_handler.getDeclaredConstructor().newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Cannot reference {@code static final T Enum.*} from enum constructor calls without qualification,
	 * so it's easier to have a separate class and static import everything.
	 * Visibility can't be private to be able to import this class.
	 */
	interface Const {
		long SECOND = 1000;
		long MINUTE = 60 * SECOND;
		long HOUR = 60 * MINUTE;
		long DAY = 24 * HOUR;
		long WEEK = 7 * DAY;
		long MONTH = 30 * DAY;
		long YEAR = 365 * DAY;
		long MISSING_TIME = 0;
		long N_A = 0;
		String MISSING_URL = null;
		String MISSING_SAMPLE = null;
		Class<? extends FeedHandler<? extends BaseFeed<?>>> MISSING_HANDLER = null;
	}
}
