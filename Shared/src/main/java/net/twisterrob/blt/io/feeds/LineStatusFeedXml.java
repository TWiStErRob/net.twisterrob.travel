package net.twisterrob.blt.io.feeds;

/**
 * The feed structure of the TrackerNet line status API.
 * @author TWiStEr
 * @see http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf#3.4.5
 * @see Feed#TubeDepartureBoardsLineStatus 
 * @see Feed#TubeDepartureBoardsLineStatusIncidents 
 */
@SuppressWarnings("unchecked")
interface LineStatusFeedXml extends FeedXmlDescriptor {
	String NS = "http://webservices.lul.co.uk/";
	interface Root extends LineStatusFeedXml {
		Class<? extends LineStatusFeedXml>[] CHILDREN = new Class[]{LineStatus.class};
		String ELEMENT = "ArrayOfLineStatus";
	}
	interface LineStatus extends LineStatusFeedXml {
		Class<? extends LineStatusFeedXml>[] CHILDREN = new Class[]{BranchDisruptions.class, Line.class, Status.class};
		String ELEMENT = "LineStatus";
		/**
		 * <code>LineStatus ID</code>: An identifier for the line.
		 */
		String id = "ID";
		/**
		 * <code>StatusDetails</code>: A description of the status of the line if the status is not normal otherwise this will be blank.
		 */
		String statusDetails = "StatusDetails";
	}
	interface BranchDisruptions extends LineStatusFeedXml {
		Class<? extends LineStatusFeedXml>[] CHILDREN = new Class[]{BranchDisruption.class};
		/**
		 * <code>BranchDisruptions</code>: Not Used.
		 */
		String ELEMENT = "BranchDisruptions";
	}
	interface BranchDisruption extends LineStatusFeedXml {
		Class<? extends LineStatusFeedXml>[] CHILDREN = NO_CHILDREN;
		String ELEMENT = "BranchDisruption";
		// TODO
	}
	interface Line extends LineStatusFeedXml {
		Class<? extends LineStatusFeedXml>[] CHILDREN = NO_CHILDREN;
		String ELEMENT = "Line";
		/**
		 * <code>Line ID</code>: A code representing the line.
		 */
		String id = "ID";
		/**
		 * <code>Name</code>: The line name.
		 */
		String name = "Name";
	}
	interface Status extends LineStatusFeedXml {
		Class<? extends LineStatusFeedXml>[] CHILDREN = new Class[]{StatusType.class};
		String ELEMENT = "Status";
		/**
		 * <code>Status ID</code>: A numeric code representing the status of the line.
		 */
		String id = "ID";
		/**
		 * <code>CssClass</code>: A text code representing the general status of the line, e.g. GoodService, DisruptedService.
		 */
		String cssClass = "CssClass";
		/**
		 * <code>Description</code>: A description of the status of the line e.g. Part Suspended, Severe Delays.
		 */
		String description = "Description";
		/**
		 * <code>IsActive</code>: A Boolean indicating if the status shown is active.
		 */
		String isActive = "IsActive";
	}
	interface StatusType extends LineStatusFeedXml {
		Class<? extends LineStatusFeedXml>[] CHILDREN = NO_CHILDREN;
		String ELEMENT = "Status";
		/**
		 * <code>StatusType ID</code>: A code representing the status type the service is checking. For this call it will always return the value “1”.
		 */
		String id = "ID";
		/**
		 * <code>Description</code>: A description of the status type the service is checking. For this call it will always return the value “Line”.
		 */
		String description = "Description";
	}
}