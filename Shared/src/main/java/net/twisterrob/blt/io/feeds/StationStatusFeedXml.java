package net.twisterrob.blt.io.feeds;

/**
 * The feed structure of the TrackerNet line status API.
 * @author TWiStEr
 * @see http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf#3.3.5
 * @see Feed#TubeDepartureBoardsStationStatus 
 * @see Feed#TubeDepartureBoardsStationStatusIncidents 
 */
@SuppressWarnings("unchecked")
interface StationStatusFeedXml extends FeedXmlDescriptor {
	String NS = "http://webservices.lul.co.uk/";
	interface Root extends StationStatusFeedXml {
		Class<? extends StationStatusFeedXml>[] CHILDREN = new Class[]{StationStatus.class};
		String ELEMENT = "ArrayOfStationStatus";
	}
	interface StationStatus extends StationStatusFeedXml {
		Class<? extends StationStatusFeedXml>[] CHILDREN = new Class[]{Station.class, Status.class};
		String ELEMENT = "StationStatus";
		/**
		 * <code>StationStatus ID</code>: An identifier for the station.
		 */
		String id = "ID";
		/**
		 * <code>StatusDetails</code>: A description of the status of the station if the status is not normal otherwise this will be blank.
		 */
		String statusDetails = "StatusDetails";
	}

	interface Station extends StationStatusFeedXml {
		Class<? extends StationStatusFeedXml>[] CHILDREN = NO_CHILDREN;
		String ELEMENT = "Station";
		/**
		 * <code>Station ID</code>: A code representing the station.
		 */
		String id = "ID";
		/**
		 * <code>Name</code>: The station name.
		 */
		String name = "Name";
	}
	interface Status extends StationStatusFeedXml {
		Class<? extends StationStatusFeedXml>[] CHILDREN = new Class[]{StatusType.class};
		String ELEMENT = "Status";
		/**
		 * <code>Status ID</code>: A numeric code representing the status of the station.
		 */
		String id = "ID";
		/**
		 * <code>CssClass</code>: A text code representing the general status of the station e.g. Open, Closed.
		 */
		String cssClass = "CssClass";
		/**
		 * <code>Description</code>: A description of the status of the station e.g. No Step Free Access.
		 */
		String description = "Description";
		/**
		 * <code>IsActive</code>: A Boolean indicating if the status shown is active.
		 */
		String isActive = "IsActive";
	}
	interface StatusType extends StationStatusFeedXml {
		Class<? extends StationStatusFeedXml>[] CHILDREN = NO_CHILDREN;
		String ELEMENT = "Status";
		/**
		 * <code>StatusType ID</code>: A code representing the status type the service is checking. For this call it will always return the value “2”.
		 */
		String id = "ID";
		/**
		 * <code>Description</code>: A description of the status type the service is checking. For this call it will always return the value “Station”.
		 */
		String description = "Description";
	}
}