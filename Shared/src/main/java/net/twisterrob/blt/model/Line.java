package net.twisterrob.blt.model;

import java.util.*;

public enum Line {
	unknown('?', StopType.unknown, "Unknown", "") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getUnknownBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getUnknownForeground();
		}
	},
	Bakerloo('B', StopType.Underground, "Bakerloo") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getBakerlooBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getBakerlooForeground();
		}
	},
	Central('C', StopType.Underground, "Central") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getCentralBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getCentralForeground();
		}
	},
	Circle('H', StopType.Underground, "Circle") { // Hammersmith & Circle?
		// H from http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf / 5. Appendix A
		@Override
		public int getBackground(LineColors colors) {
			return colors.getCircleBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getCircleForeground();
		}
	},
	District('D', StopType.Underground, "District") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getDistrictBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getDistrictForeground();
		}
	},
	HammersmithAndCity('H', StopType.Underground, "H'smith & City", "Hammersmith & City", "Hammersmith and City") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getHammersmithAndCityBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getHammersmithAndCityForeground();
		}
	},
	Jubilee('J', StopType.Underground, "Jubilee") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getJubileeBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getJubileeForeground();
		}
	},
	Metropolitan('M', StopType.Underground, "Metropolitan") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getMetropolitanBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getMetropolitanForeground();
		}
	},
	Northern('N', StopType.Underground, "Northern") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getNorthernBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getNorthernForeground();
		}
	},
	Piccadilly('P', StopType.Underground, "Piccadilly") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getPiccadillyBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getPiccadillyForeground();
		}
	},
	Victoria('V', StopType.Underground, "Victoria") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getVictoriaBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getVictoriaForeground();
		}
	},
	WaterlooAndCity('W', StopType.Underground, "Waterloo & City", "Waterloo and City") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getWaterlooAndCityBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getWaterlooAndCityForeground();
		}
	},
	DLR('L', StopType.DLR, "DLR", "Docklands Light Railway") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getDLRBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getDLRForeground();
		}
	},
	Overground('O', StopType.Overground, "Overground", "East London") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getOvergroundBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getOvergroundForeground();
		}
	},
	Tram('T', StopType.Tram, "Tram", "Tramlink 1", "Tramlink 2", "Tramlink 3", "Tramlink 4") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getTramBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getTramForeground();
		}
	},
	EmiratesAirline('E', StopType.Air, "Emirates Air Line", "Emirates Airline") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getEmiratesBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getEmiratesForeground();
		}
	};
	private char m_code;
	private List<String> m_aliases;
	private StopType m_defaultStopType;

	private Line(char code, StopType defaultStopType, String... aliases) {
		m_code = code;
		m_defaultStopType = defaultStopType;
		m_aliases = Arrays.asList(aliases);
	}

	public String getTrackerNetCode() {
		return "" + m_code;
	}

	public StopType getDefaultStopType() {
		return m_defaultStopType;
	}

	public String getTitle() {
		return m_aliases.get(0);
	}

	public abstract int getBackground(LineColors colors);
	public abstract int getForeground(LineColors colors);

	public static Line fromAlias(String alias) {
		for (Line line: values()) {
			if (line.m_aliases.contains(alias)) {
				return line;
			}
		}
		return Line.unknown;
	}
	public static Line fromTrackerNetCode(char code) {
		for (Line line: values()) {
			if (line.m_code == code) {
				return line;
			}
		}
		return Line.unknown;
	}

	public static final Set<Line> UNDERGROUND = Collections.unmodifiableSet(EnumSet.of(Bakerloo, Central, Circle,
			District, HammersmithAndCity, Jubilee, Metropolitan, Northern, Piccadilly, Victoria, WaterlooAndCity));

	public static <T, L extends List<? extends T>, M extends Map<Line, ? super L>> M fixMapList(M map, L empty) {
		for (Line line: values()) {
			if (!map.containsKey(line)) {
				map.put(line, empty);
			}
		}
		return map;
	}
	public static <T, S extends Set<? extends T>, M extends Map<Line, ? super S>> M fixMapSet(M map, S empty) {
		for (Line line: values()) {
			if (!map.containsKey(line)) {
				map.put(line, empty);
			}
		}
		return map;
	}
	public static <K, V, MM extends Map<? extends K, ? extends V>, M extends Map<Line, ? super MM>> M fixMapMap(M map,
			MM empty) {
		for (Line line: values()) {
			if (!map.containsKey(line)) {
				map.put(line, empty);
			}
		}
		return map;
	}
}
