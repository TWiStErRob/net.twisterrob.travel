package net.twisterrob.blt.io.feeds;

/**
 * The feed structure of the TrackerNet line status API.
 * @author TWiStEr
 * @see http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf#3.4.5
 * @see Feed#TubeDepartureBoardsLineStatus 
 * @see Feed#TubeDepartureBoardsLineStatusIncidents 
 */
interface LineStatusFeedXml extends FeedXmlDescriptor {
	@Children(LineStatus.class)
	interface Root {
		String NS = "http://webservices.lul.co.uk/";
		String ELEMENT = "ArrayOfLineStatus";
	}

	@Children({BranchDisruptions.class, Line.class, Status.class})
	interface LineStatus {
		String ELEMENT = "LineStatus";
		/**
		 * <code>LineStatus ID</code>: An identifier for the line.
		 */
		@Attribute String id = "ID";
		/**
		 * <code>StatusDetails</code>: A description of the status of the line if the status is not normal otherwise this will be blank.
		 */
		@Attribute String statusDetails = "StatusDetails";
	}

	@Children(BranchDisruption.class)
	interface BranchDisruptions {
		/**
		 * <code>BranchDisruptions</code>: Not Used.
		 */
		String ELEMENT = "BranchDisruptions";
	}

	interface BranchDisruption {
		String ELEMENT = "BranchDisruption";
		// TODO
	}

	interface Line {
		String ELEMENT = "Line";
		/**
		 * <code>Line ID</code>: A code representing the line.
		 */
		@Attribute String id = "ID";
		/**
		 * <code>Name</code>: The line name.
		 */
		@Attribute String name = "Name";
	}

	@Children(StatusType.class)
	interface Status {
		String ELEMENT = "Status";
		/**
		 * <code>Status ID</code>: A numeric code representing the status of the line.
		 */
		@Attribute String id = "ID";
		/**
		 * <code>CssClass</code>: A text code representing the general status of the line, e.g. GoodService, DisruptedService.
		 */
		@Attribute String cssClass = "CssClass";
		/**
		 * <code>Description</code>: A description of the status of the line e.g. Part Suspended, Severe Delays.
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
		 * <code>StatusType ID</code>: A code representing the status type the service is checking. For this call it will always return the value “1”.
		 */
		@Attribute String id = "ID";
		/**
		 * <code>Description</code>: A description of the status type the service is checking. For this call it will always return the value “Line”.
		 */
		@Attribute String description = "Description";
	}
}