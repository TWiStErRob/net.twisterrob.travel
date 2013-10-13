package net.twisterrob.blt.io.feeds;

/**
 * The feed structure of the TrackerNet prediction summary API.
 * @author TWiStEr
 * @see http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf#3.1.5
 * @see Feed#TubeDepartureBoardsPredictionSummary 
 */
@SuppressWarnings("unchecked")
interface PredicitonSummaryFeedXml extends FeedXmlDescriptor {
	String NS = "";
	interface Root extends PredicitonSummaryFeedXml {
		Class<? extends PredicitonSummaryFeedXml>[] CHILDREN = new Class[]{Time.class, Station.class};
		String ELEMENT = "ROOT";
	}
	interface Time extends PredicitonSummaryFeedXml {
		String ELEMENT = "Time";
		/**
		 * <code>Time TimeStamp</code>: The date/time the service was run in the format YYYY/MM/DD HH:MM:SS.
		 * @see #timeStamp$format
		 */
		String timeStamp = "TimeStamp";
		/**
		 * @see #timeStamp
		 */
		String timeStamp$format = "yyyy/MM/dd HH:mm:ss";
	}
	/**
	 * <code>S(tation)</code>: A construct representing a station on the line.
	 */
	interface Station extends PredicitonSummaryFeedXml {
		Class<? extends PredicitonSummaryFeedXml>[] CHILDREN = new Class[]{Platform.class};
		/**
		 * <code>S(tation)</code>: A construct representing a station on the line.
		 */
		String ELEMENT = "S";
		/**
		 * <code>Code</code>: A code representing the station (see Appendix B for valid values).
		 */
		String code = "Code";
		/**
		 * <code>N(ame)</code>: The name of the station.
		 */
		String name = "N";
	}
	/**
	 * <code>P(latform)</code>: A construct representing a platform on the station.
	 */
	interface Platform extends PredicitonSummaryFeedXml {
		Class<? extends PredicitonSummaryFeedXml>[] CHILDREN = new Class[]{Train.class};
		/**
		 * <code>P(latform)</code>: A construct representing a platform on the station.
		 */
		String ELEMENT = "P";
		/**
		 * <code>N(ame)</code>: The name of the platform.
		 */
		String name = "N";
		/**
		 * <code>C(ode)</code>: A code representing the platform.
		 */
		String code = "Code";
	}
	/**
	 * <code>T(rain)</code>: A construct representing a train in the prediction list.
	 */
	interface Train extends PredicitonSummaryFeedXml {
		Class<? extends PredicitonSummaryFeedXml>[] CHILDREN = NO_CHILDREN;
		/**
		 * <code>T(rain)</code>: A construct representing a train in the prediction list.
		 */
		String ELEMENT = "T";
		/**
		 * <code>S(et number)</code>: The set number of the train.
		 */
		String setNumber = "S";
		/**
		 * <code>T(rip number)</code>: The trip number of the train.
		 */
		String tripNumber = "T";
		/**
		 * <code>D(estination)</code>: A code representing the destination of the train.
		 */
		String destinationCode = "D";
		/**
		 * <code>C(?)</code>: A value in representing the ‘time to station’ for this train in the format MM:SS.
		 * @see #timeToStation$format
		 * @see #timeToStation$atPlatform
		 */
		String timeToStation = "C";
		/**
		 * @see #timeToStation
		 */
		String timeToStation$format = "m:ss";
		/**
		 * @see #timeToStation
		 */
		String timeToStation$atPlatform = "-";
		/**
		 * <code>L(ocation)</code>: The current location of the train.
		 */
		String Location = "L";
		/**
		 * <code>DE(stination)</code>: The name of the destination of the train.
		 */
		String destinationName = "DE";
	}
}