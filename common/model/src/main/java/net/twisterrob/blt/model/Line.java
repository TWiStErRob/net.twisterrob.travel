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
	unknown(StopType.unknown),
	Bakerloo(StopType.Underground),
	Central(StopType.Underground),
	// H from http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf / 5. Appendix A
	Circle(StopType.Underground), // Hammersmith & Circle?
	District(StopType.Underground),
	HammersmithAndCity(StopType.Underground),
	Jubilee(StopType.Underground),
	Metropolitan(StopType.Underground),
	Northern(StopType.Underground),
	Piccadilly(StopType.Underground),
	Victoria(StopType.Underground),
	WaterlooAndCity(StopType.Underground),
	DLR(StopType.DLR),
	Overground(StopType.Overground),
	Tram(StopType.Tram),
	EmiratesAirline(StopType.Air),
	// TODO what is the tracker net code?
	/**
	 * It has been rebranded to {@link #ElizabethLine}.
	 * @see <a href="https://tfl.gov.uk/info-for/media/press-releases/2022/may/elizabeth-line-to-open-on-24-may-2022">News</a>
	 */
	@Deprecated
	TflRail(StopType.Rail),
	// "Elizabeth line" first to display it (match https://tfl.gov.uk/tube-dlr-overground/status/)
	// "Elizabeth Line" comes from XML feed (http://cloud.tfl.gov.uk/TrackerNet/LineStatus)
	// "Elizabeth" came from XML feed before 2022-11-07 so required to parse historical data.
	// TODO what is the tracker net code?
	ElizabethLine(StopType.Rail),
	;
	private static final int NO_ID = 0;

	private final StopType defaultStopType;

	Line(StopType defaultStopType) {
		this.defaultStopType = defaultStopType;
	}

	public StopType getDefaultStopType() {
		return defaultStopType;
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
