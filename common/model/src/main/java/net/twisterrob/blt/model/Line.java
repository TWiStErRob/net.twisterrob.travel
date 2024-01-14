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
	unknown(Line.NO_ID, StopType.unknown),
	Bakerloo(1, StopType.Underground),
	Central(2, StopType.Underground),
	// H from http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf / 5. Appendix A
	Circle(7, StopType.Underground), // Hammersmith & Circle?
	District(9, StopType.Underground),
	HammersmithAndCity(8, StopType.Underground),
	Jubilee(4, StopType.Underground),
	Metropolitan(11, StopType.Underground),
	Northern(5, StopType.Underground),
	Piccadilly(6, StopType.Underground),
	Victoria(3, StopType.Underground),
	WaterlooAndCity(12, StopType.Underground),
	DLR(81, StopType.DLR),
	Overground(82, StopType.Overground),
	Tram(90, StopType.Tram),
	EmiratesAirline(Line.NO_ID, StopType.Air),
	// TODO what is the tracker net code?
	/**
	 * It has been rebranded to {@link #ElizabethLine}.
	 * @see <a href="https://tfl.gov.uk/info-for/media/press-releases/2022/may/elizabeth-line-to-open-on-24-may-2022">News</a>
	 */
	@Deprecated
	TflRail(83, StopType.Rail),
	// "Elizabeth line" first to display it (match https://tfl.gov.uk/tube-dlr-overground/status/)
	// "Elizabeth Line" comes from XML feed (http://cloud.tfl.gov.uk/TrackerNet/LineStatus)
	// "Elizabeth" came from XML feed before 2022-11-07 so required to parse historical data.
	// TODO what is the tracker net code?
	ElizabethLine(83, StopType.Rail),
	;
	private static final int NO_ID = 0;

	/**
	 * {@code ID} attribute of {@code <Line>} in the Line Status feed.
	 */
	private final int lineID;
	private final StopType defaultStopType;

	Line(int lineID, StopType defaultStopType) {
		this.lineID = lineID;
		this.defaultStopType = defaultStopType;
	}

	public StopType getDefaultStopType() {
		return defaultStopType;
	}

	public int getLineStatusLineID() {
		return lineID;
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
