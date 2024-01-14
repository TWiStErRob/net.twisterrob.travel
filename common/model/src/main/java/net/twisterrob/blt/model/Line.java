package net.twisterrob.blt.model;

import java.util.*;

public enum Line {
	unknown(StopType.unknown),
	Bakerloo(StopType.Underground),
	Central(StopType.Underground),
	Circle(StopType.Underground),
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
	/**
	 * It has been rebranded to {@link #ElizabethLine}.
	 * @see <a href="https://tfl.gov.uk/info-for/media/press-releases/2022/may/elizabeth-line-to-open-on-24-may-2022">News</a>
	 */
	TflRail(StopType.Rail),
	ElizabethLine(StopType.Rail),
	;

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
