package net.twisterrob.blt.model;

import java.util.Arrays;

public enum Line {
	Bakerloo('B', "Bakerloo") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getBakerlooBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getBakerlooForeground();
		}
	},
	Central('C', "Central") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getCentralBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getCentralForeground();
		}
	},
	Circle('H', "Circle") { // Hammersmith & Circle?
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
	District('D', "District") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getDistrictBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getDistrictForeground();
		}
	},
	HammersmithAndCity('H', "H'smith & City", "Hammersmith & City", "Hammersmith and City") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getHammersmithAndCityBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getHammersmithAndCityForeground();
		}
	},
	Jubilee('J', "Jubilee") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getJubileeBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getJubileeForeground();
		}
	},
	Metropolitan('M', "Metropolitan") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getMetropolitanBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getMetropolitanForeground();
		}
	},
	Northern('N', "Northern") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getNorthernBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getNorthernForeground();
		}
	},
	Piccadilly('P', "Piccadilly") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getPiccadillyBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getPiccadillyForeground();
		}
	},
	Victoria('V', "Victoria") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getVictoriaBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getVictoriaForeground();
		}
	},
	WaterlooAndCity('W', "Waterloo & City", "Waterloo and City") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getWaterlooAndCityBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getWaterlooAndCityForeground();
		}
	},
	DLR('-', "DLR", "Docklands Light Railway") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getDLRBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getDLRForeground();
		}
	},
	Overground('-', "Overground", "East London") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getOvergroundBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getOvergroundForeground();
		}
	},
	Tram('?', "Tram") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getTramBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getTramForeground();
		}
	},
	EmiratesAirline('?', "Emirates Airline") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getEmiratesBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getEmiratesForeground();
		}
	},
	unknown('?', "Unknown", "") {
		@Override
		public int getBackground(LineColors colors) {
			return colors.getUnknownBackground();
		}
		@Override
		public int getForeground(LineColors colors) {
			return colors.getUnknownForeground();
		}
	};
	private char m_code;
	private String[] m_aliases;

	private Line(char code, String... aliases) {
		m_code = code;
		m_aliases = aliases;
	}

	public String getTrackerNetCode() {
		return "" + m_code;
	}

	public String getTitle() {
		return m_aliases[0];
	}

	public abstract int getBackground(LineColors colors);
	public abstract int getForeground(LineColors colors);

	public static Line fromAlias(String alias) {
		for (Line line: values()) {
			if (Arrays.asList(line.m_aliases).contains(alias)) {
				return line;
			}
		}
		return Line.unknown;
	}
}
