package net.twisterrob.blt.io.feeds.trackernet;

import net.twisterrob.blt.io.feeds.FeedXmlDescriptor;

/**
 * The feed structure of the TrackerNet line status API.http://content.tfl.gov.uk/trackernet-data-services-guide-beta.pdf
 * @see <a href="http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf">
 *     Trackernet_Data_Services_Guide_Beta_0_2.pdf @ 08/12/2010 > 3.4.5</a>
 * @see <a href="http://content.tfl.gov.uk/trackernet-data-services-guide-beta.pdf">
 *     In 2020: Trackernet_Data_Services_Guide_Beta_0_2.pdf @ 08/12/2010 > 3.4.5</a>
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsLineStatus
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsLineStatusIncidents
 */
@SuppressWarnings("UnnecessaryInterfaceModifier")
/*default*/ interface LineStatusFeedXml extends FeedXmlDescriptor {
	@Children(LineStatus.class)
	public interface Root {
		String NS = "http://webservices.lul.co.uk/";
		String ELEMENT = "ArrayOfLineStatus";
	}

	@Children({BranchDisruptions.class, Line.class, Status.class})
	public interface LineStatus {
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
	public interface BranchDisruptions {
		/**
		 * <code>BranchDisruptions</code>: Not Used.
		 */
		String ELEMENT = "BranchDisruptions";
	}

	// TODO compiler keeps bailing on this
	//@Children({Status.class, StationTo.class, StationFrom.class})
	public interface BranchDisruption {
		String ELEMENT = "BranchDisruption";

		public interface StationTo {
			String ELEMENT = "StationTo";
			@Attribute String id = "ID";
			@Attribute String name = "Name";
		}

		public interface StationFrom {
			String ELEMENT = "StationFrom";
			@Attribute String id = "ID";
			@Attribute String name = "Name";
		}
	}

	public interface Line {
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
	public interface Status {
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

	public interface StatusType {
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
