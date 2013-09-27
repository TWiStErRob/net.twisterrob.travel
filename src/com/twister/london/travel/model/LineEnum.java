package com.twister.london.travel.model;

import java.util.Arrays;

import android.graphics.Color;

/**
 * Colors: http://www.tfl.gov.uk/assets/downloads/corporate/tfl-colour-standard-issue03.pdf
 */
public enum LineEnum {
	Bakerloo('B', Color.rgb(137, 78, 36), "Bakerloo"),
	Central('C', Color.rgb(220, 36, 31), "Central"),
	Circle('?', Color.rgb(255, 206, 0), "Circle"),
	District('D', Color.rgb(0, 114, 41), "District"),
	HammersmithAndCity('H', Color.rgb(215, 153, 175), "Hammersmith and City"),
	Jubilee('J', Color.rgb(134, 143, 152), "Jubilee"),
	Metropolitan('M', Color.rgb(117, 16, 86), "Metropolitan"),
	Northern('N', Color.rgb(0, 0, 0), "Northern"),
	Piccadilly('P', Color.rgb(0, 25, 168), "Piccadilly"),
	Victoria('V', Color.rgb(0, 160, 226), "Victoria"),
	WaterlooAndCity('W', Color.rgb(118, 208, 189), "Waterloo and City"),
	Overground('-', Color.rgb(232, 106, 16), "Overground"),
	DLR('-', Color.rgb(0, 175, 173), "DLR"),
	unknown('-', Color.rgb(255, 255, 255), "Unknown", "");
	private char m_code;
	private int m_color;
	private String[] m_aliases;

	private LineEnum(char code, int color, String... aliases) {
		m_code = code;
		m_color = color;
		m_aliases = aliases;
	}

	public char getCode() {
		return m_code;
	}

	public int getColor() {
		return m_color;
	}

	public String getTitle() {
		return m_aliases[0];
	}

	public static LineEnum fromAlias(String alias) {
		for (LineEnum line: values()) {
			if (Arrays.asList(line.m_aliases).contains(alias)) {
				return line;
			}
		}
		return LineEnum.unknown;
	}
}
