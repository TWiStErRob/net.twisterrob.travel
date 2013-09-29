package net.twisterrob.blt.model;

import java.util.Arrays;

public enum Line {
	Bakerloo('B', "Bakerloo") {
		public int getBackground(LineColors colors) {
			return colors.getBakerlooBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getBakerlooForeground();
		}
	},
	Central('C', "Central") {
		public int getBackground(LineColors colors) {
			return colors.getCentralBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getCentralForeground();
		}
	},
	Circle('?', "Circle") {
		public int getBackground(LineColors colors) {
			return colors.getCircleBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getCircleForeground();
		}
	},
	District('D', "District") {
		public int getBackground(LineColors colors) {
			return colors.getDistrictBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getDistrictForeground();
		}
	},
	DLR('-', "DLR", "Docklands Light Railway") {
		public int getBackground(LineColors colors) {
			return colors.getDLRBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getDLRForeground();
		}
	},
	HammersmithAndCity('H', "H'smith & City", "Hammersmith & City", "Hammersmith and City") {
		public int getBackground(LineColors colors) {
			return colors.getHammersmithAndCityBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getHammersmithAndCityForeground();
		}
	},
	Jubilee('J', "Jubilee") {
		public int getBackground(LineColors colors) {
			return colors.getJubileeBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getJubileeForeground();
		}
	},
	Metropolitan('M', "Metropolitan") {
		public int getBackground(LineColors colors) {
			return colors.getMetropolitanBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getMetropolitanForeground();
		}
	},
	Northern('N', "Northern") {
		public int getBackground(LineColors colors) {
			return colors.getNorthernBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getNorthernForeground();
		}
	},
	Overground('-', "Overground") {
		public int getBackground(LineColors colors) {
			return colors.getOvergroundBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getOvergroundForeground();
		}
	},
	Piccadilly('P', "Piccadilly") {
		public int getBackground(LineColors colors) {
			return colors.getPiccadillyBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getPiccadillyForeground();
		}
	},
	Victoria('V', "Victoria") {
		public int getBackground(LineColors colors) {
			return colors.getVictoriaBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getVictoriaForeground();
		}
	},
	WaterlooAndCity('W', "Waterloo & City", "Waterloo and City") {
		public int getBackground(LineColors colors) {
			return colors.getWaterlooAndCityBackground();
		}
		public int getForeground(LineColors colors) {
			return colors.getWaterlooAndCityForeground();
		}
	},
	unknown('-', "Unknown", "") {
		public int getBackground(LineColors colors) {
			return colors.getUnknownBackground();
		}
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
