package net.twisterrob.blt.io.feeds.trackernet;

import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.java.annotations.SimpleDateFormatString;

/**
 * The feed structure of the TrackerNet prediction summary API.
 * @author TWiStEr
 * @see http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf#3.1.5
 * @see Feed#TubeDepartureBoardsPredictionSummary 
 */
interface PredictionSummaryFeedXml extends FeedXmlDescriptor {
	@Children({Time.class, Station.class})
	interface Root {
		String NS = "";
		String ELEMENT = "ROOT";
	}
	interface Time {
		String ELEMENT = "Time";
		/**
		 * <code>Time TimeStamp</code>: The date/time the service was run in the format YYYY/MM/DD HH:MM:SS.
		 * @see #timeStamp$format
		 */
		@Attribute String timeStamp = "TimeStamp";
		/**
		 * @see #timeStamp
		 */
		@ValueConstraint @SimpleDateFormatString String timeStamp$format = "yyyy/MM/dd HH:mm:ss";
	}
	/**
	 * <code>S(tation)</code>: A construct representing a station on the line.
	 */
	@Children(Platform.class)
	interface Station {
		/**
		 * <code>S(tation)</code>: A construct representing a station on the line.
		 */
		String ELEMENT = "S";
		/**
		 * <code>Code</code>: A code representing the station (see Appendix B for valid values).
		 */
		@Attribute String code = "Code";
		/**
		 * <code>N(ame)</code>: The name of the station.
		 */
		@Attribute String name = "N";
	}
	/**
	 * <code>P(latform)</code>: A construct representing a platform on the station.
	 */
	@Children(Train.class)
	interface Platform {
		/**
		 * <code>P(latform)</code>: A construct representing a platform on the station.
		 */
		String ELEMENT = "P";
		/**
		 * <code>N(ame)</code>: The name of the platform.
		 */
		@Attribute String name = "N";
		/**
		 * <code>C(ode)</code>: A code representing the platform.
		 */
		@Attribute String code = "Code";
	}
	/**
	 * <code>T(rain)</code>: A construct representing a train in the prediction list.
	 */
	interface Train {
		/**
		 * <code>T(rain)</code>: A construct representing a train in the prediction list.
		 */
		String ELEMENT = "T";
		/**
		 * <code>S(et number)</code>: The set number of the train.
		 */
		@Attribute String setNumber = "S";
		/**
		 * <code>T(rip number)</code>: The trip number of the train.
		 */
		@Attribute String tripNumber = "T";
		/**
		 * <code>D(estination)</code>: A code representing the destination of the train.
		 */
		@Attribute String destinationCode = "D";
		/**
		 * <code>C(?)</code>: A value in representing the ‘time to station’ for this train in the format MM:SS.
		 * @see #timeToStation$format
		 * @see #timeToStation$atPlatform
		 */
		@Attribute String timeToStation = "C";
		/**
		 * @see #timeToStation
		 */
		@ValueConstraint @SimpleDateFormatString String timeToStation$format = "m:ss";
		/**
		 * @see #timeToStation
		 */
		@ValueConstraint String timeToStation$atPlatform = "-";
		/**
		 * @see #timeToStation
		 */
		@ValueConstraint String timeToStation$due = "due";
		/**
		 * <code>L(ocation)</code>: The current location of the train.
		 */
		@Attribute String Location = "L";
		/**
		 * <code>DE(stination)</code>: The name of the destination of the train.
		 */
		@Attribute String destinationName = "DE";
	}
}