package com.twister.london.travel.model;

import java.util.*;

/**
 * @author TWiStEr
 */
public class TubeStatusPresentationLineColors implements LineColors {
	public String getLineColorsSource() {
		return "http://www.tfl.gov.uk/assets/downloads/businessandpartners/tube-status-presentation-user-guide.pdf";
	}
	public String getLineColorsName() {
		return "User Guide for the presentation of Service updates";
	}
	public String getLineColorsVersion() {
		return "TfL Online User Guide Version 2";
	}
	public Date getLineColorsDate() {
		return new GregorianCalendar(2012, 3, 0).getTime();
	}

	public int getBakerlooBackground() {
		return parseColor("#AE6118");
	}

	public int getBakerlooForeground() {
		return parseColor("#FFFFFF");
	}

	public int getCentralBackground() {
		return parseColor("#E41F1F");
	}

	public int getCentralForeground() {
		return parseColor("#FFFFFF");
	}

	public int getCircleBackground() {
		return parseColor("#F8D42D");
	}

	public int getCircleForeground() {
		return parseColor("#113892");
	}

	public int getDistrictBackground() {
		return parseColor("#007229");
	}

	public int getDistrictForeground() {
		return parseColor("#FFFFFF");
	}

	public int getDLRBackground() {
		return parseColor("#00BBB4");
	}

	public int getDLRForeground() {
		return parseColor("#FFFFFF");
	}

	public int getHammersmithAndCityBackground() {
		return parseColor("#E899A8");
	}

	public int getHammersmithAndCityForeground() {
		return parseColor("#113892");
	}

	public int getJubileeBackground() {
		return parseColor("#686E72");
	}

	public int getJubileeForeground() {
		return parseColor("#FFFFFF");
	}

	public int getMetropolitanBackground() {
		return parseColor("#893267");
	}

	public int getMetropolitanForeground() {
		return parseColor("#FFFFFF");
	}

	public int getNorthernBackground() {
		return parseColor("#000000");
	}

	public int getNorthernForeground() {
		return parseColor("#FFFFFF");
	}

	public int getOvergroundBackground() {
		return parseColor("#F86C00"); // from image, text says #F86
	}

	public int getOvergroundForeground() {
		return parseColor("#FFFFFF");
	}

	public int getPiccadillyBackground() {
		return parseColor("#113892");
	}

	public int getPiccadillyForeground() {
		return parseColor("#FFFFFF");
	}

	public int getVictoriaBackground() {
		return parseColor("#009FE0");
	}

	public int getVictoriaForeground() {
		return parseColor("#FFFFFF");
	}

	public int getWaterlooAndCityBackground() {
		return parseColor("#70C3C3");
	}

	public int getWaterlooAndCityForeground() {
		return parseColor("#113892");
	}

	public int getUnknownBackground() {
		return parseColor("#FFFFFF");
	}

	public int getUnknownForeground() {
		return parseColor("#000000");
	}

	private static int parseColor(String html) {
		return Integer.parseInt(html.substring(1), 16);
	}
}
