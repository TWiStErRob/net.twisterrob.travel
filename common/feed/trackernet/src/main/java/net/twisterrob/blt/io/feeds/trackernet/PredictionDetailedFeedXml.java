package net.twisterrob.blt.io.feeds.trackernet;

import net.twisterrob.blt.io.feeds.FeedXmlDescriptor;
import net.twisterrob.java.annotations.SimpleDateFormatString;

/**
 * The feed structure of the TrackerNet prediction details API.
 * @see <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">
 *     Trackernet_Data_Services_Guide_Beta_0_2.pdf > 3.2.5</a>
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsPredictionDetailed
 */
@SuppressWarnings("UnnecessaryInterfaceModifier")
/*default*/ interface PredictionDetailedFeedXml extends FeedXmlDescriptor {
	public interface Root {
		String NS = "http://trackernet.lul.co.uk";
		String ELEMENT = "ROOT";
		/**
		 * <code>Disclaimer</code>: A disclaimer about the information only nature of the service.
		 */
		@Child String Disclaimer = "Disclaimer";

		/**
		 * <code>WhenCreated</code>: The time/date the service was run in the format DD MMM YYYY HH:MM:SS.
		 */
		@Child String WhenCreated = "WhenCreated";
		@ValueConstraint @SimpleDateFormatString String WhenCreated$format = "d MMM yyyy H:mm:ss";

		/**
		 * <code>Line</code>: A code representing the line (see Appendix A).
		 */
		@Child String Line = "Line";

		/**
		 * <code>LineName</code>: The name of the line.
		 */
		@Child String LineName = "LineName";

		/**
		 * <code>S(tation)</code>: A construct representing a station on the line.
		 */
		@Child(Station.class) String Station = PredictionDetailedFeedXml.Station.ELEMENT;
	}

	/**
	 * <code>S(tation)</code>: A construct representing a station on the line.
	 */
	@Children(Platform.class)
	public interface Station {
		/**
		 * <code>S(tation)</code>: A construct representing a station on the line.
		 */
		String ELEMENT = "S";
		/**
		 * <code>Code</code>: A code representing the station (see Appendix B for valid values).
		 */
		@Attribute String code = "Code";
		/**
		 * <code>Mess</code>: .
		 */
		@Attribute String message = "Mess";
		/**
		 * <code>N(ame)</code>: The name of the station.
		 */
		@Attribute String name = "N";
		/**
		 * <code>CurTime</code>: The time the service was run in the format HH:MM:SS.
		 */
		@Attribute String currentTime = "CurTime";
		String currentTime$format = "H:mm:ss";
	}

	/**
	 * <code>P(latform)</code>: A construct representing a platform on the station.
	 */
	@Children(Train.class)
	public interface Platform {
		/**
		 * <code>P(latform)</code>: A construct representing a platform on the station.
		 */
		String ELEMENT = "P";
		/**
		 * <code>N(ame)</code>: The name of the platform.
		 */
		@Attribute String name = "N";
		/**
		 * <code>Num</code>: The platform number.
		 */
		@Attribute String number = "Num";
		/**
		 * <code>TrackCode</code>: The track code of the section of track at the front of the platform.
		 */
		@Attribute String trackCode = "TrackCode";
	}

	/**
	 * <code>T(rain)</code>: A construct representing a train in the prediction list.
	 */
	public interface Train {
		Class<?>[] CHILDREN = NO_CHILDREN;
		/**
		 * <code>T(rain)</code>: A construct representing a train in the prediction list.
		 */
		String ELEMENT = "T";
		/**
		 * <code>LCID</code>: The leading car Id for the train.
		 */
		@Attribute String leadingCarID = "LCID";
		/**
		 * <code>SetNo</code>: The set number of the train.
		 */
		@Attribute String setNumber = "SetNo";
		/**
		 * <code>TripNo</code>: The trip number of the train.
		 */
		@Attribute String tripNumber = "TripNo";
		/**
		 * <code>SecondsTo</code>: A value representing the ‘time to station’ for this train in seconds in the format SSS.
		 * @see #secondsTo$format
		 */
		@Attribute String secondsTo = "SecondsTo";
		/**
		 * @see #secondsTo
		 */
		@Attribute @SimpleDateFormatString String secondsTo$format = "s";
		/**
		 * <code>TimeTo</code>: A value representing the ‘time to station’ for this train in minutes and seconds in the format MM:SS.
		 * @see #timeTo$format
		 * @see #timeTo$atPlatform
		 */
		@Attribute String timeTo = "TimeTo";
		/**
		 * @see #timeTo
		 */
		@ValueConstraint @SimpleDateFormatString String timeTo$format = "m:ss";
		/**
		 * @see #timeTo
		 */
		@ValueConstraint @SimpleDateFormatString String timeTo$atPlatform = "-";
		/**
		 * <code>Location</code>: The current location of the train.
		 */
		@Attribute String Location = "Location";
		/**
		 * <code>Destination</code>: The name of the destination of the train.
		 */
		@Attribute String destinationName = "Destination";
		/**
		 * <code>DestCode</code>: A code representing the destination of the train.
		 */
		@Attribute String destinationCode = "DestCode";
		/**
		 * <code>Order</code>: Not Assigned. Value will default to zero.
		 */
		@Attribute String order = "Order";
		/**
		 * <code>DepartTime</code>: Time train departed the platform.
		 * @see #departedTime$format
		 */
		@Attribute String departedTime = "DepartTime";
		/**
		 * @see #departedTime
		 */
		@ValueConstraint @SimpleDateFormatString String departedTime$format = "H:mm:ss";
		/**
		 * <code>DepartInterval</code>: Interval in seconds between the departure of the specified train and the previous train.
		 */
		@Attribute String departureInterval = "DepartInterval";
		/**
		 * @see #departureInterval
		 */
		@ValueConstraint @SimpleDateFormatString String departureInterval$format = "s";
		/**
		 * <code>Departed</code>: Boolean value to determine if the train has departed the platform.
		 * @see #isDeparted$true
		 * @see #isDeparted$false
		 */
		@Attribute String isDeparted = "Departed";
		/**
		 * @see #isDeparted
		 */
		@ValueConstraint String isDeparted$true = "1";
		/**
		 * @see #isDeparted
		 */
		@ValueConstraint String isDeparted$false = "0";
		/**
		 * <code>Direction</code>: Direction of Travel.
		 */
		@Attribute String direction = "Direction";
		/**
		 * <code>IsStalled</code>: Not Assigned. Value will default to zero.
		 * @see #isStalled$true
		 * @see #isStalled$false
		 */
		@Attribute String isStalled = "IsStalled";
		/**
		 * @see #isStalled
		 */
		@ValueConstraint String isStalled$true = "1";
		/**
		 * @see #isStalled
		 */
		@ValueConstraint String isStalled$false = "0";
		/**
		 * <code>TrackCode</code>: The current section of track the train occupies.
		 */
		@Attribute String trackerCode = "TrackerCode";
		/**
		 * <code>LN</code>: A code representing the line the train is running on (see Appendix A).
		 */
		@Attribute String lineCode = "LN";
	}
}
