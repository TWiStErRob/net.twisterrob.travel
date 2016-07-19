package net.twisterrob.blt.io.feeds.trackernet;

import static java.util.Collections.*;

import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.java.utils.CollectionTools;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.model.Line;

class StationIncosistencies {
	public static final Map<Line, Map<String, String>> TIMETABLE_TO_TRACKERNET_ALIASES = unmodifiableMap(initAliases());
	public static final Map<Line, Map<String, String>> TRACKERNET_TO_TIMETABLE_ALIASES = unmodifiableMap(reverseAliases(TIMETABLE_TO_TRACKERNET_ALIASES));
	public static final Map<Line, Map<String, Line>> EXTAS = unmodifiableMap(initExtras());

	/**
	 * Map for each line from how a station is called in the {@link Feed#JourneyPlannerTimetables}
	 * to how it is called in the {@link Feed#TubeDepartureBoards*} feeds.
	 */
	private static Map<Line, Map<String, String>> initAliases() {
		Map<Line, Map<String, String>> aliases = new EnumMap<>(Line.class);
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.Bakerloo, unmodifiableMap(stationNames)); // has extras
			stationNames.put("Edgware Road", "Edgware Road (Bakerloo)");
			stationNames.put("Harrow & Wealdstone Station", "Harrow & Wealdstone");
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.Central, unmodifiableMap(stationNames));
			stationNames.put("Shepherd's Bush (Central Line)", "Shepherds Bush (Central Line)");
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.Circle, unmodifiableMap(stationNames)); // has extras
			stationNames.put("Bromley-By-Bow", "Bromley-by-Bow");
			stationNames.put("Earl's Court", null);
			stationNames.put("Edgware Road", "Edgware Road (H & C)");
			stationNames.put("Goldhawk Road", null);
			stationNames.put("Hammersmith (Ham & City Line)", "Hammersmith (C&H)");
			stationNames.put("King's Cross St.Pancras", "King's Cross St. Pancras");
			stationNames.put("Latimer Road", null);
			stationNames.put("Paddington", "Paddington Circle");
			stationNames.put("Paddington (H&C Line)", "Paddington H & C");
			stationNames.put("Shepherd's Bush Market", null);
			stationNames.put("St James's Park", "St. James's Park");
			stationNames.put("Wood Lane", null);
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.District, unmodifiableMap(stationNames));
			stationNames.put("Bayswater", null);
			stationNames.put("Bromley-By-Bow", "Bromley-by-Bow");
			stationNames.put("Dagenham Heathway", null); // Dagenham East?
			stationNames.put("Earl's Court", "Earls Court");
			stationNames.put("Edgware Road", "Edgware Road (H & C)");
			stationNames.put("Hammersmith", "Hammersmith (District and Picc)");
			stationNames.put("Kensington (Olympia)", "Olympia");
			stationNames.put("Notting Hill Gate", null);
			stationNames.put("Paddington", "Paddington Circle");
			stationNames.put("St James's Park", "St. James's Park");
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.HammersmithAndCity, unmodifiableMap(stationNames)); // has extras
			stationNames.put("Bromley-By-Bow", "Bromley-by-Bow");
			stationNames.put("Edgware Road", "Edgware Road (H & C)");
			stationNames.put("Goldhawk Road", null);
			stationNames.put("Hammersmith (Ham & City Line)", "Hammersmith (C&H)");
			stationNames.put("King's Cross St.Pancras", "King's Cross St. Pancras");
			stationNames.put("Latimer Road", null);
			stationNames.put("Paddington (H&C Line)", "Paddington H & C");
			stationNames.put("Shepherd's Bush Market", null);
			stationNames.put("Wood Lane", null);
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.Jubilee, unmodifiableMap(stationNames));
			stationNames.put("London Bridge Station", "London Bridge");
			stationNames.put("St.John's Wood", "St. John's Wood");
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.Metropolitan, unmodifiableMap(stationNames)); // has extras
			stationNames.put("Harrow-on-the-Hill", "Harrow on the Hill");
			stationNames.put("King's Cross St.Pancras", "King's Cross St. Pancras");
			stationNames.put("Preston Road", null);
			stationNames.put("Watford Underground Station", "Watford");
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.Northern, unmodifiableMap(stationNames));
			stationNames.put("Borough Station", "Borough");
			stationNames.put("Brent Cross Station", "Brent Cross");
			stationNames.put("Hampstead Station", "Hampstead");
			stationNames.put("Kennington Station", "Kennington");
			stationNames.put("King's Cross St.Pancras", "King's Cross St. Pancras");
			stationNames.put("London Bridge Station", "London Bridge");
			stationNames.put("Tooting Bec Station", "Tooting Bec");
			stationNames.put("Totteridge & Whetstone", "Totteridge and Whetstone");
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.Victoria, unmodifiableMap(stationNames));
			stationNames.put("King's Cross St.Pancras", "King's Cross St. Pancras");
		}
		{
			Map<String, String> stationNames = new HashMap<>();
			aliases.put(Line.Piccadilly, unmodifiableMap(stationNames));
			stationNames.put("Earl's Court", "Earls Court");
			stationNames.put("Hammersmith", "Hammersmith (District and Picc)");
			stationNames.put("Heathrow Terminals 1-3", "Heathrow Terminals 123");
			stationNames.put("King's Cross St.Pancras", "King's Cross St. Pancras");
			stationNames.put("Wood Green Station", "Wood Green");
		}
		return Line.fixMap(aliases, Collections.<String, String> emptyMap());
	}

	private static Map<Line, Map<String, String>> reverseAliases(
			Map<Line, ? extends Map<String, String>> timetableToTrackernetAliases) {
		Map<Line, Map<String, String>> extras = new EnumMap<>(Line.class);
		for (Entry<Line, ? extends Map<String, String>> entry: timetableToTrackernetAliases.entrySet()) {
			Map<String, String> reverse = CollectionTools.reverseMap(entry.getValue(), new HashMap<String, String>());
			extras.put(entry.getKey(), reverse);
		}
		return Line.fixMap(extras, Collections.<String, String> emptyMap());
	}
	private static Map<Line, Map<String, Line>> initExtras() {
		Map<Line, Map<String, Line>> extras = new EnumMap<>(Line.class);
		{
			Map<String, Line> stationNames = new TreeMap<>();
			extras.put(Line.Bakerloo, unmodifiableMap(stationNames));
			stationNames.put("Bushey", Line.Overground);
			stationNames.put("Carpenders Park", Line.Overground);
			stationNames.put("Hatch End", Line.Overground);
			stationNames.put("Headstone Lane", Line.Overground);
			stationNames.put("Watford High Street", Line.Overground);
			stationNames.put("Watford Junction", Line.Overground);
		}
		{
			Map<String, Line> stationNames = new TreeMap<>();
			extras.put(Line.Circle, unmodifiableMap(stationNames));
			stationNames.put("Finchley Road", Line.Metropolitan);
			stationNames.put("Harrow on the Hill", Line.Metropolitan);
			stationNames.put("Moor Park", Line.Metropolitan);
			stationNames.put("Neasden", Line.Jubilee); // Trains are Metropolitan
			stationNames.put("North Harrow", Line.Metropolitan);
			stationNames.put("Northwick Park", Line.Metropolitan);
			stationNames.put("Northwood", Line.Metropolitan);
			stationNames.put("Northwood Hills", Line.Metropolitan);
			stationNames.put("Pinner", Line.Metropolitan);
			stationNames.put("Rayners Lane", Line.Metropolitan);
			stationNames.put("Rickmansworth", Line.Metropolitan);
			stationNames.put("Wembley Park", Line.Metropolitan);
			stationNames.put("West Harrow", Line.Metropolitan);
			stationNames.put("Willesden Green", Line.Jubilee); // Trains are Metropolitan
		}
		{
			Map<String, Line> stationNames = new TreeMap<>();
			extras.put(Line.HammersmithAndCity, unmodifiableMap(stationNames));
			stationNames.put("Aldgate", Line.Circle);
			stationNames.put("Bayswater", Line.Circle);
			stationNames.put("Blackfriars", Line.Circle);
			stationNames.put("Cannon Street", Line.Circle);
			stationNames.put("Embankment", Line.Circle);
			stationNames.put("Gloucester Road", Line.Circle);
			stationNames.put("High Street Kensington", Line.Circle);
			stationNames.put("Mansion House", Line.Circle);
			stationNames.put("Monument", Line.Circle);
			stationNames.put("Notting Hill Gate", Line.Circle);
			stationNames.put("Paddington Circle", Line.Circle);
			stationNames.put("Sloane Square", Line.Circle);
			stationNames.put("South Kensington", Line.Circle);
			stationNames.put("St. James's Park", Line.Circle);
			stationNames.put("Temple", Line.Circle);
			stationNames.put("Tower Hill", Line.Circle);
			stationNames.put("Victoria", Line.Circle);
			stationNames.put("Westminster", Line.Circle);

			stationNames.put("Finchley Road", Line.Metropolitan);
			stationNames.put("Harrow on the Hill", Line.Metropolitan);
			stationNames.put("Moor Park", Line.Metropolitan);
			stationNames.put("Neasden", Line.Metropolitan);
			stationNames.put("North Harrow", Line.Metropolitan);
			stationNames.put("Northwick Park", Line.Metropolitan);
			stationNames.put("Northwood", Line.Metropolitan);
			stationNames.put("Northwood Hills", Line.Metropolitan);
			stationNames.put("Pinner", Line.Metropolitan);
			stationNames.put("Rayners Lane", Line.Metropolitan);
			stationNames.put("Rickmansworth", Line.Metropolitan);
			stationNames.put("Wembley Park", Line.Metropolitan);
			stationNames.put("West Harrow", Line.Metropolitan);
			stationNames.put("Willesden Green", Line.Metropolitan);
		}
		{
			Map<String, Line> stationNames = new TreeMap<>();
			extras.put(Line.Metropolitan, unmodifiableMap(stationNames));
			stationNames.put("Barking", Line.District);
			stationNames.put("Colliers Wood", Line.Northern);
		}
		return Line.fixMap(extras, Collections.<String, Line> emptyMap());
	}
}
