package net.twisterrob.blt.io.feeds.timetable;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.twisterrob.blt.io.feeds.timetable.JourneyPlannerTimetableFeed.RouteFixer;

// FIXME merge this and StationInconsistencies
public class StationNameFixer implements RouteFixer {
	private static final Logger LOG = LoggerFactory.getLogger(StationNameFixer.class);
	private static final Pattern FIXER = Pattern.compile("^Emirates Air Line | (?:"
			+ "Station(?: \\(London\\))?"
			+ "|Tram(?:link)? Stop"
			+ "|(?:DLR|Underground)(?: Station)?"
			+ "|\\((?:Ham & City|Central|H&C) Line\\)"
			+ ")$");

	public boolean matches(JourneyPlannerTimetableFeed feed) {
		return true;
	}

	public void fix(JourneyPlannerTimetableFeed feed) {
		Pattern fixer = Pattern.compile(FIXER.pattern()); // local copy
		for (Route route : feed.getRoutes()) {
			for (StopPoint stop : route.getStops()) {
				stop.setName(fix(fixer, stop.getName()));
			}
		}
	}
	public static String fix(String original) {
		return fix(FIXER, original);
	}
	private static String fix(Pattern fixer, final String original) {
		String fixed = original;
		fixed = fixer.matcher(fixed).replaceFirst("");
		if (!original.equals(fixed)) {
			LOG.trace("Changed station name \"{}\" to \"{}\".", original, fixed);
		}
		return fixed;
	}
}
