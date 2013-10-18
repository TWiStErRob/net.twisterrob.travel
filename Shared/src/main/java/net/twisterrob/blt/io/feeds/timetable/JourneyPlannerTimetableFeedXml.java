package net.twisterrob.blt.io.feeds.timetable;

import net.twisterrob.blt.io.feeds.*;

/**
 * TODO
 * @author TWiStEr
 * @see http://www.transxchange.org.uk/schema/2.1/TransXChange_general.xsd
 * @see http://www.transxchange.org.uk/schema/2.1/TransXChange_common.xsd
 * @see Feed#JourneyPlannerTimetables 
 */
@SuppressWarnings("hiding")
interface JourneyPlannerTimetableFeedXml extends FeedXmlDescriptor {
	interface Root {
		String NS = "http://www.transxchange.org.uk/";
		Class<?>[] CHILDREN = new Class[]{StopAreas.class, JourneyPatternSections.class, Operators.class,
				Services.class, VehicleJourneys.class, Registrations.class, SupportingDocuments.class};
		String ELEMENT = "TransXChange";
		String modificationTime = "ModificationDateTime";
		String modificationTime$format = "yyyy-MM-dd'T'HH:mm:ss.SXXX";

		/**
		 * Names of Nptg Localities used in Local stop definitions.
		 * Allows an optional local copy of name data for localities taken from the NPTG.
		 * Locality names can be included in a TransXChange document to support the Publishing of stop names
		 * that include the locality name eg "Barset, High Street".
		 * Locality names of externally referenced NaPTAN stops should be included in the AnnotatedStopPointRef instead.
		 * The XSD Type name is: TransXChangeNptgLocalitiesStructure
		 * @see AnnotatedNptgLocalityRef
		 */
		String NptgLocalities = "NptgLocalities";

		/**
		 * Local Stop definitions for Stops used in services.
		 * Normally Stops will be defined simply by referencing their NaPTAN AtcoCode in a StopPointRef.
		 * Stops can also be defined locally here.
		 * The XSD Type name is: TransXChangeStopPointsStructure
		 * @see StopPoint
		 */
		String StopPoints = "StopPoints";

		/**
		 * Definitions of the Route Sections, i.e. collections of  of route links, making up all or part of a Route.
		 * These are used in Route definitions.
		 * The XSD Type name is: RouteSectionsStructure
		 * @see RouteSection
		 */
		String RouteSections = "RouteSections";

		/**
		 * Definitions of transport Routes..
		 * The XSD Type name is: RoutesStructure
		 * @see Route
		 */
		String Routes = "Routes";
	}
	/**
	 * A NPTG locality reference annotated by its names.
	 * TransXChange document to support the Publishing of stop names that include the locality name eg "Barset, High Street".
	 * The XSD Type name is: AnnotatedNptgLocalityRefStructure
	 */
	interface AnnotatedNptgLocalityRef {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "AnnotatedNptgLocalityRef";
		/**
		 * Unique identifier of the locality..
		 * The XSD Type name is: NptgLocalityRefStructure
		 */
		String ID = "NptgLocalityRef";

		/**
		 * Name of NPTG Locality in which one ore more locally defined stop in this document lies.
		 * To be used when publishing stop names.
		 * The XSD Type name is: NaturalLanguageStringStructure
		 */
		String Name = "LocalityName";

		/**
		 * Any qualifier of NPTG Locality in which stop lies..
		 * The XSD Type name is: NaturalLanguageStringStructure
		 */
		String Qualifier = "LocalityQualifier";
	}

	/**
	 * A NaPTAN stop definition.
	 * The XSD Type name is: StopPointStructure
	 */
	interface StopPoint {
		Class<?>[] CHILDREN = new Class[]{Descriptor.class, Place.class, StopClassification.class};
		String ELEMENT = "StopPoint";
		String creationTime = "CreationDateTime";
		String creationTime$format = "yyyy-MM-dd'T'HH:mm:ss";
		/**
		 * Full NaPTAN stop identifier that uniquely identifies the stop within the UK.
		 */
		String AtcoCode = "AtcoCode";
		/**
		 * NPTG administrative area that manages stop data. 000-999.
		 */
		String AdminstrativeAreaRef = "AdminstrativeAreaRef";
		/**
		 * Notes about a stop.
		 */
		String Notes = "Notes";

		/**
		 * Structured textual description of stop..
		 * The XSD Type name is: DescriptorStructure
		 */
		interface Descriptor {
			Class<?>[] CHILDREN = new Class[]{};
			String ELEMENT = "Descriptor";
			/**
			 * Common name for the stop in a specified language.
			 */
			String CommonName = "CommonName";
		}
		/**
		 * Place where stop is located.
		 * The XSD Type name is: inline element Place
		 */
		interface Place {
			Class<?>[] CHILDREN = new Class[]{Location.class};
			String ELEMENT = "Place";
			/**
			 * NPTG locality within which stop lies.
			 * The XSD Type name is: NptgLocalityRefStructure
			 */
			String NptgLocalityRef = "NptgLocalityRef";

			/**
			 * Spatial coordinates of stop.
			 * The XSD Type name is: LocationStructure
			 */
			interface Location {
				Class<?>[] CHILDREN = new Class[]{};
				String ELEMENT = "Location";
				/**
				 * Precision of geocoding.
				 * @see PrecisionEnum
				 */
				String precision = "Precision";
				/**
				 * OS 1 metre - 6 digits, max: 999999
				 */
				String Easting = "Easting";
				/**
				 * OS 1 metre - 6/7 digits, max: 1999999
				 */
				String Northing = "Northing";

				enum PrecisionEnum {
					_1km(1000),
					_100m(100),
					_10m(10),
					_1m(1);
					private int meters;

					private PrecisionEnum(int meters) {
						this.meters = meters;
					}
					public int getMeters() {
						return meters;
					}
					public static PrecisionEnum valueOfXml(String name) {
						return valueOf("_" + name);
					}
				}
			}
		}
		/**
		 * Classification, e.g. on-street bus stop; platform at a railway station.
		 * The XSD Type name is: StopClassificationStructure
		 */
		interface StopClassification {
			Class<?>[] CHILDREN = new Class[]{};
			String ELEMENT = "StopClassification";
			/**
			 * Classification of the stop as one of the NaPTAN stop types.
			 * @see StopTypeEnum
			 */
			String StopType = "StopType";

			// TODO OnStreet/OffStreet

			/**
			 * Classification of the stop as one of the NaPTAN stop types. 
			 */
			enum StopTypeEnum {
				AIR("airportEntrance", "Airport Entrance."),
				GAT("airAccessArea", "Air Airside Area."),
				FTD("ferryTerminalDockEntrance", "Ferry Terminal / Dock Entrance."),
				FER("ferryDockAccessArea", "Ferry / Dock Berth Area."),
				FBT("FerryBerth", ""),
				RSE("railStationEntrance", "Rail Station Entrance."),
				RLY("railAccessArea", "Rail Platform Access Area."),
				RPL("railPlatform", ""),
				TMU("tramMetroUndergroundStationEntrance", "Tram / Metro / Underground Entrance."),
				MET("tramMetroUndergroundAccessArea", ""),
				PLT("tramMetroUndergroundPlatform", "Metro and Underground   Platform Access Area."),
				BCE("busCoachTramStationEntrance", "Bus / Coach Station Entrance."),
				BST("busCoachStationAccessArea", ""),
				BCS("busCoachTramStationBay", "Bus / Coach bay / stand / stance within Bus / Coach Stations."),
				BCQ("busCoachTramStationVariableBay", ""),
				BCT("busCoachTramOnStreetPoint", "On street Bus / Coach / Tram Stop."),
				TXR("taxiRank", "Taxi Rank (head of)."),
				STR("sharedTaxiRank", "Shared Taxi Rank (head of).");
				private String longName;
				private String description;

				private StopTypeEnum(String longName, String description) {
					this.longName = longName;
					this.description = description;
				}

				public String getLongName() {
					return longName;
				}
				public String getDescription() {
					return description;
				}
			}
		}
	}

	/**
	 * A NaPTAN stop reference annotated by common name..
	 * The XSD Type name is: AnnotatedStopPointRefStructure
	 */
	interface AnnotatedStopPointRef {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "AnnotatedStopPointRef";
	}
	/**
	 * Local StopArea definitions. Normally StopAreas will be defined as part of their NaPTAN Definition. Areas can be defined locally here..
	 * The XSD Type name is: StopAreasStructure
	 */
	@Deprecated
	interface StopAreas {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "StopAreas";
	}

	/**
	 * A reusable section of route comprising one or more route links, ordered in sequence of traversal.
	 * Definitions of the Route Sections, i.e. collections of  of route links, making up all or part of a Route. These are used in Route definitions.
	 * The XSD Type name is: RouteSectionsStructure/RouteSection
	 */
	interface RouteSection {
		Class<?>[] CHILDREN = new Class[]{RouteLink.class};
		String ELEMENT = "RouteSection";
		String id = "id";
	}

	/**
	 * A piece of the network topology connection two stops.
	 * The XSD Type name is: RouteSectionsStructure/RouteSection/RouteLink
	 */
	interface RouteLink {
		Class<?>[] CHILDREN = new Class[]{From.class, To.class};
		String ELEMENT = "RouteLink";
		String id = "id";
		/**
		 * Distance in metres along track of the link.
		 */
		String Distance = "Distance";
		/**
		 * Direction of the route running over the link. Enumerated value.
		 * @see DirectionEnum
		 */
		String Direction = "Direction";
		enum DirectionEnum {
			inbound,
			outbound,
			clockwise,
			anticlockwise
		}
		/**
		 * The stop at which the link begins.
		 */
		interface From {
			Class<?>[] CHILDREN = new Class[]{};
			String ELEMENT = "From";
			/**
			 * Reference to a NaPTAN stop. <code>[0-9]{3}[A-Za-z0-9]{2,9}</code>	
			 */
			String StopPointRef = "StopPointRef";
		}
		/**
		 * The stop at which the link ends.
		 */
		interface To {
			Class<?>[] CHILDREN = new Class[]{};
			String ELEMENT = "To";
			/**
			 * Reference to a NaPTAN stop. <code>[0-9]{3}[A-Za-z0-9]{2,9}</code>
			 */
			String StopPointRef = "StopPointRef";
		}
	}

	/**
	 * A path over the transport network topology that a bus takes to deliver a service.
	 * An ordered collection of links connecting NaPTAN stops; the links are grouped into sections.
	 * The XSD Type name is: RoutesStructure/Route
	 */
	interface Route {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "Route";
		String id = "id";
		/**
		 * Short description of the Route.
		 */
		String Description = "Description";
		/**
		 * An ordered collection of RouteSections that make up the Route. The order is the sequence of traversal.
		 */
		String RouteSectionRef = "RouteSectionRef";
	}

	/**
	 * Definitions of journey pattern sections..
	 * The XSD Type name is: JourneyPatternSectionsStructure
	 */
	interface JourneyPatternSections {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "JourneyPatternSections";
	}
	/**
	 * Definitions of operators..
	 * The XSD Type name is: OperatorsStructure
	 */
	@Deprecated
	interface Operators {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "Operators";
	}
	/**
	 * Definitions of services..
	 * The XSD Type name is: ServicesStructure
	 */
	@Deprecated
	interface Services {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "Services";
	}
	/**
	 * Definitions of vehicle journeys, used in one or more services..
	 * The XSD Type name is: VehicleJourneysStructure
	 */
	@Deprecated
	interface VehicleJourneys {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "VehicleJourneys";
	}
	/**
	 * Registration details..
	 * The XSD Type name is: RegistrationsStructure
	 */
	@Deprecated
	interface Registrations {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "Registrations";
	}
	/**
	 * Additional documents describing the service..
	 * The XSD Type name is: SupportingDocumentsStructure
	 */
	@Deprecated
	interface SupportingDocuments {
		Class<?>[] CHILDREN = new Class[]{};
		String ELEMENT = "SupportingDocuments";
	}
}