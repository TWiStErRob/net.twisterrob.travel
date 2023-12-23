package net.twisterrob.blt.model;

import java.util.*;

/**
 * Possible lines that may appear in line status, for example:
 * <pre><code>&lt;Line ID="8" Name="Hammersmith and City" /&gt;</code></pre>
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsLineStatus
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsLineStatusIncidents
 * @see <a href="http://cloud.tfl.gov.uk/TrackerNet/LineStatus">Current status</a>
 */
public enum Line {
	unknown('?', Line.NO_ID, StopType.unknown, "Unknown", "") {
		@Override public int getBackground(LineColors colors) {
			return colors.getUnknownBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getUnknownForeground();
		}
	},
	Bakerloo('B', 1, StopType.Underground, "Bakerloo") {
		@Override public int getBackground(LineColors colors) {
			return colors.getBakerlooBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getBakerlooForeground();
		}
	},
	Central('C', 2, StopType.Underground, "Central") {
		@Override public int getBackground(LineColors colors) {
			return colors.getCentralBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getCentralForeground();
		}
	},
	Circle('H', 7, StopType.Underground, "Circle") { // Hammersmith & Circle?
		// H from http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf / 5. Appendix A
		@Override public int getBackground(LineColors colors) {
			return colors.getCircleBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getCircleForeground();
		}
	},
	District('D', 9, StopType.Underground, "District") {
		@Override public int getBackground(LineColors colors) {
			return colors.getDistrictBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getDistrictForeground();
		}
	},
	HammersmithAndCity('H', 8, StopType.Underground, "Hammersmith & City", "H'smith & City", "Hammersmith and City") {
		@Override public int getBackground(LineColors colors) {
			return colors.getHammersmithAndCityBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getHammersmithAndCityForeground();
		}
	},
	Jubilee('J', 4, StopType.Underground, "Jubilee") {
		@Override public int getBackground(LineColors colors) {
			return colors.getJubileeBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getJubileeForeground();
		}
	},
	Metropolitan('M', 11, StopType.Underground, "Metropolitan") {
		@Override public int getBackground(LineColors colors) {
			return colors.getMetropolitanBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getMetropolitanForeground();
		}
	},
	Northern('N', 5, StopType.Underground, "Northern") {
		@Override public int getBackground(LineColors colors) {
			return colors.getNorthernBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getNorthernForeground();
		}
	},
	Piccadilly('P', 6, StopType.Underground, "Piccadilly") {
		@Override public int getBackground(LineColors colors) {
			return colors.getPiccadillyBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getPiccadillyForeground();
		}
	},
	Victoria('V', 3, StopType.Underground, "Victoria") {
		@Override public int getBackground(LineColors colors) {
			return colors.getVictoriaBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getVictoriaForeground();
		}
	},
	WaterlooAndCity('W', 12, StopType.Underground, "Waterloo & City", "Waterloo and City") {
		@Override public int getBackground(LineColors colors) {
			return colors.getWaterlooAndCityBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getWaterlooAndCityForeground();
		}
	},
	DLR('L', 81, StopType.DLR, "DLR", "Docklands Light Railway") {
		@Override public int getBackground(LineColors colors) {
			return colors.getDLRBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getDLRForeground();
		}
	},
	Overground('O', 82, StopType.Overground, "Overground", "East London") {
		@Override public int getBackground(LineColors colors) {
			return colors.getOvergroundBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getOvergroundForeground();
		}
	},
	Tram('T', 90, StopType.Tram, "Trams", "Tram", "Tramlink 1", "Tramlink 2", "Tramlink 3", "Tramlink 4") {
		@Override public int getBackground(LineColors colors) {
			return colors.getTramBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getTramForeground();
		}
	},
	EmiratesAirline('E', Line.NO_ID, StopType.Air, "Emirates Air Line", "Emirates Airline") {
		@Override public int getBackground(LineColors colors) {
			return colors.getEmiratesBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getEmiratesForeground();
		}
	},
	// TODO what is the tracker net code?
	/**
	 * It has been rebranded to {@link #ElizabethLine}.
	 * @see <a href="https://tfl.gov.uk/info-for/media/press-releases/2022/may/elizabeth-line-to-open-on-24-may-2022">News</a>
	 */
	@Deprecated
	TflRail('?', 83, StopType.Rail, "TfL Rail") {
		@Override public int getBackground(LineColors colors) {
			return colors.getTfLRailBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getTfLRailForeground();
		}
	},
	// "Elizabeth line" first to display it (match https://tfl.gov.uk/tube-dlr-overground/status/)
	// "Elizabeth Line" comes from XML feed (http://cloud.tfl.gov.uk/TrackerNet/LineStatus)
	// "Elizabeth" came from XML feed before 2022-11-07 so required to parse historical data.
	// TODO what is the tracker net code?
	ElizabethLine('?', 83, StopType.Rail, "Elizabeth line", "Elizabeth Line", "Elizabeth") {
		@Override public int getBackground(LineColors colors) {
			return colors.getElizabethLineBackground();
		}
		@Override public int getForeground(LineColors colors) {
			return colors.getElizabethLineForeground();
		}
	},
	;
	private static final int NO_ID = 0;

	/**
	 * TODO ???
	 */
	private final char code;
	/**
	 * Possible names this Line can appear as.
	 * <ul>
	 * <li>{@code Name} attribute of {@code <Line>} in the Line Status feed.</li>
	 * </ul>
	 */
	private final List<String> aliases;
	/**
	 * {@code ID} attribute of {@code <Line>} in the Line Status feed.
	 */
	private final int lineID;
	private final StopType defaultStopType;

	Line(char code, int lineID, StopType defaultStopType, String... aliases) {
		this.code = code;
		this.lineID = lineID;
		this.defaultStopType = defaultStopType;
		this.aliases = Arrays.asList(aliases);
	}

	public String getTrackerNetCode() {
		return "" + code;
	}

	public StopType getDefaultStopType() {
		return defaultStopType;
	}

	public String getTitle() {
		return aliases.get(0);
	}

	public int getLineStatusLineID() {
		return lineID;
	}

	public abstract int getBackground(LineColors colors);
	public abstract int getForeground(LineColors colors);

	public static Line fromEnumName(String line) {
		try {
			return Line.valueOf(line);
		} catch (IllegalArgumentException ex) {
			return Line.unknown;
		}
	}
	public static Line fromAlias(String alias) {
		for (Line line : values()) {
			if (line.aliases.contains(alias)) {
				return line;
			}
		}
		return Line.unknown;
	}
	public static Line fromTrackerNetCode(char code) {
		for (Line line : values()) {
			if (line.code == code) {
				return line;
			}
		}
		return Line.unknown;
	}

	public static final Set<Line> UNDERGROUND = Collections.unmodifiableSet(EnumSet.of(Bakerloo, Central, Circle,
			District, HammersmithAndCity, Jubilee, Metropolitan, Northern, Piccadilly, Victoria, WaterlooAndCity));

	public static <T, M extends Map<Line, ? super T>> M fixMap(M map, T empty) {
		for (Line line : values()) {
			if (!map.containsKey(line)) {
				map.put(line, empty);
			}
		}
		return map;
	}
}
