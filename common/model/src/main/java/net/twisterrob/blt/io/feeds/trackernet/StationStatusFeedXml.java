package net.twisterrob.blt.io.feeds.trackernet;

import net.twisterrob.blt.io.feeds.FeedXmlDescriptor;

/**
 * The feed structure of the TrackerNet line status API.
 * @see <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">
 *     Trackernet_Data_Services_Guide_Beta_0_2.pdf > 3.3.5</a>
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsStationStatus
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsStationStatusIncidents
 */
@SuppressWarnings("UnnecessaryInterfaceModifier") 
/*default*/ interface StationStatusFeedXml extends FeedXmlDescriptor {
	@Children(StationStatus.class) interface Root {
		String NS = "http://webservices.lul.co.uk/";
		String ELEMENT = "ArrayOfStationStatus";
	}

	@Children({Station.class, Status.class})
	public interface StationStatus {
		String ELEMENT = "StationStatus";
		/**
		 * <code>StationStatus ID</code>: An identifier for the station.
		 */
		@Attribute String id = "ID";
		/**
		 * <code>StatusDetails</code>: A description of the status of the station if the status is not normal otherwise this will be blank.
		 */
		@Attribute String statusDetails = "StatusDetails";
	}

	interface Station {
		String ELEMENT = "Station";
		/**
		 * <code>Station ID</code>: A code representing the station.
		 */
		@Attribute String id = "ID";
		/**
		 * <code>Name</code>: The station name.
		 */
		@Attribute String name = "Name";
	}

	@Children(StatusType.class)
	public interface Status {
		String ELEMENT = "Status";
		/**
		 * <code>Status ID</code>: A numeric code representing the status of the station.
		 */
		@Attribute String id = "ID";
		/**
		 * <code>CssClass</code>: A text code representing the general status of the station e.g. Open, Closed.
		 */
		@Attribute String cssClass = "CssClass";
		/**
		 * <code>Description</code>: A description of the status of the station e.g. No Step Free Access.
		 */
		@Attribute String description = "Description";
		/**
		 * <code>IsActive</code>: A Boolean indicating if the status shown is active.
		 */
		@Attribute String isActive = "IsActive";
	}

	public interface StatusType {
		String ELEMENT = "Status";
		/**
		 * <code>StatusType ID</code>: A code representing the status type the service is checking. For this call it will always return the value “2”.
		 */
		@Attribute String id = "ID";
		/**
		 * <code>Description</code>: A description of the status type the service is checking. For this call it will always return the value “Station”.
		 */
		@Attribute String description = "Description";
	}
}
