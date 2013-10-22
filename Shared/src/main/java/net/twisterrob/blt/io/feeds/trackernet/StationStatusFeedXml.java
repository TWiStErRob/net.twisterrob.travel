package net.twisterrob.blt.io.feeds.trackernet;

import net.twisterrob.blt.io.feeds.*;

/**
 * The feed structure of the TrackerNet line status API.
 * @author TWiStEr
 * @see http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf#3.3.5
 * @see Feed#TubeDepartureBoardsStationStatus 
 * @see Feed#TubeDepartureBoardsStationStatusIncidents 
 */
interface StationStatusFeedXml extends FeedXmlDescriptor {
	@Children(StationStatus.class)
	interface Root {
		String NS = "http://webservices.lul.co.uk/";
		String ELEMENT = "ArrayOfStationStatus";
	}
	@Children({Station.class, Status.class})
	interface StationStatus {
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
	interface Status {
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
	interface StatusType {
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