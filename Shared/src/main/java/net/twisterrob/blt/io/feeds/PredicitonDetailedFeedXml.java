package net.twisterrob.blt.io.feeds;

/**
 * The feed structure of the TrackerNet prediction details API.
 * @author TWiStEr
 * @see http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf#3.2.5
 * @see Feed#TubeDepartureBoardsPredictionDetailed
 */
@SuppressWarnings("unchecked")
interface PredicitonDetailedFeedXml extends FeedXmlDescriptor {
	String NS = "http://trackernet.lul.co.uk/";
	interface Root extends PredicitonDetailedFeedXml {
		String ELEMENT = "ROOT";
		Class<? extends PredicitonDetailedFeedXml>[] CHILDREN = new Class[]{Disclaimer.class, WhenCreated.class,
				Line.class, LineName.class, Station.class};
	}

	/**
	 * <code>Disclaimer</code>: A disclaimer about the information only nature of the service.
	 */
	interface Disclaimer extends PredicitonDetailedFeedXml {
		Class<? extends PredicitonDetailedFeedXml>[] CHILDREN = new Class[]{CONTENT.class};
		/**
		 * <code>Disclaimer</code>: A disclaimer about the information only nature of the service.
		 */
		String ELEMENT = "Disclaimer";
	}
	/**
	 * <code>WhenCreated</code>: The time/date the service was run in the format DD MMM YYYY HH:MM:SS.
	 */
	interface WhenCreated extends PredicitonDetailedFeedXml {
		Class<? extends PredicitonDetailedFeedXml>[] CHILDREN = new Class[]{CONTENT.class};
		/**
		 * <code>WhenCreated</code>: The time/date the service was run in the format DD MMM YYYY HH:MM:SS.
		 */
		String ELEMENT = "WhenCreated";
		String CONTENT$format = "d MMM yyyy H:mm:ss";
	}
	/**
	 * <code>Line</code>: A code representing the line (see Appendix A).
	 */
	interface Line extends PredicitonDetailedFeedXml {
		Class<? extends PredicitonDetailedFeedXml>[] CHILDREN = new Class[]{CONTENT.class};
		/**
		 * <code>Line</code>: A code representing the line (see Appendix A).
		 */
		String ELEMENT = "Line";
	}
	/**
	 * <code>LineName</code>: The name of the line.
	 */
	interface LineName extends PredicitonDetailedFeedXml {
		Class<? extends PredicitonDetailedFeedXml>[] CHILDREN = new Class[]{CONTENT.class};
		/**
		 * <code>LineName</code>: The name of the line.
		 */
		String ELEMENT = "LineName";
	}
	/**
	 * <code>S(tation)</code>: A construct representing a station on the line.
	 */
	interface Station extends PredicitonDetailedFeedXml {
		Class<? extends PredicitonDetailedFeedXml>[] CHILDREN = new Class[]{Platform.class};
		/**
		 * <code>S(tation)</code>: A construct representing a station on the line.
		 */
		String ELEMENT = "S";
		/**
		 * <code>Code</code>: A code representing the station (see Appendix B for valid values).
		 */
		String code = "Code";
		/**
		 * <code>Mess</code>: .
		 */
		String message = "Mess";
		/**
		 * <code>N(ame)</code>: The name of the station.
		 */
		String name = "N";
		/**
		 * <code>CurTime</code>: The time the service was run in the format HH:MM:SS.
		 */
		String currentTime = "CurTime";
		String currentTime$format = "H:mm:ss";
	}
	/**
	 * <code>P(latform)</code>: A construct representing a platform on the station.
	 */
	interface Platform extends PredicitonDetailedFeedXml {
		Class<? extends PredicitonDetailedFeedXml>[] CHILDREN = new Class[]{Train.class};
		/**
		 * <code>P(latform)</code>: A construct representing a platform on the station.
		 */
		String ELEMENT = "P";
		/**
		 * <code>N(ame)</code>: The name of the platform.
		 */
		String name = "N";
		/**
		 * <code>Num</code>: The platform number.
		 */
		String number = "Num";
		/**
		 * <code>TrackCode</code>: The track code of the section of track at the front of the platform.
		 */
		String trackCode = "TrackCode";
	}
	/**
	 * <code>T(rain)</code>: A construct representing a train in the prediction list.
	 */
	interface Train extends PredicitonDetailedFeedXml {
		Class<? extends PredicitonDetailedFeedXml>[] CHILDREN = NO_CHILDREN;
		/**
		 * <code>T(rain)</code>: A construct representing a train in the prediction list.
		 */
		String ELEMENT = "T";
		/**
		 * <code>LCID</code>: The leading car Id for the train.
		 */
		String leadingCarID = "LCID";
		/**
		 * <code>SetNo</code>: The set number of the train.
		 */
		String setNumber = "SetNo";
		/**
		 * <code>TripNo</code>: The trip number of the train.
		 */
		String tripNumber = "TripNo";
		/**
		 * <code>SecondsTo</code>: A value representing the ‘time to station’ for this train in seconds in the format SSS.
		 * @see #secondsTo$format
		 */
		String secondsTo = "SecondsTo";
		/**
		 * @see #secondsTo
		 */
		String secondsTo$format = "s";
		/**
		 * <code>TimeTo</code>: A value representing the ‘time to station’ for this train in minutes and seconds in the format MM:SS.
		 * @see #timeTo$format
		 */
		String timeTo = "TimeTo";
		/**
		 * @see #timeTo
		 */
		String timeTo$format = "m:ss";
		/**
		 * <code>Location</code>: The current location of the train.
		 */
		String Location = "Location";
		/**
		 * <code>Destination</code>: The name of the destination of the train.
		 */
		String destinationName = "Destination";
		/**
		 * <code>DestCode</code>: A code representing the destination of the train.
		 */
		String destinationCode = "DestCode";
		/**
		 * <code>Order</code>: Not Assigned. Value will default to zero.
		 */
		String order = "Order";
		/**
		 * <code>DepartTime</code>: Time train departed the platform.
		 * @see #departedTime$format
		 */
		String departedTime = "DepartTime";
		/**
		 * @see #departedTime
		 */
		String departedTime$format = "H:mm:ss";
		/**
		 * <code>DepartInterval</code>: Interval in seconds between the departure of the specified train and the previous train.
		 */
		String departureInterval = "DepartInterval";
		/**
		 * @see #departureInterval
		 */
		String departureInterval$format = "s";
		/**
		 * <code>Departed</code>: Boolean value to determine if the train has departed the platform.
		 * @see #isDeparted$true
		 * @see #isDeparted$false
		 */
		String isDeparted = "Departed";
		/**
		 * @see #isDeparted
		 */
		String isDeparted$true = "1";
		/**
		 * @see #isDeparted
		 */
		String isDeparted$false = "0";
		/**
		 * <code>Direction</code>: Direction of Travel.
		 */
		String direction = "Direction";
		/**
		 * <code>IsStalled</code>: Not Assigned. Value will default to zero.
		 * @see #isStalled$true
		 * @see #isStalled$false
		 */
		String isStalled = "IsStalled";
		/**
		 * @see #isStalled
		 */
		String isStalled$true = "1";
		/**
		 * @see #isStalled
		 */
		String isStalled$false = "0";
		/**
		 * <code>TrackCode</code>: The current section of track the train occupies.
		 */
		String trackerCode = "TrackerCode";
		/**
		 * <code>LN</code>: A code representing the line the train is running on (see Appendix A).
		 */
		String lineCode = "LN";
	}
}