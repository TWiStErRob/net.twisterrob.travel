package com.twister.london.travel.model;

import java.util.*;

import android.graphics.Color;

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
		return Color.parseColor("#AE6118");
	}

	public int getBakerlooForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getCentralBackground() {
		return Color.parseColor("#E41F1F");
	}

	public int getCentralForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getCircleBackground() {
		return Color.parseColor("#F8D42D");
	}

	public int getCircleForeground() {
		return Color.parseColor("#113892");
	}

	public int getDistrictBackground() {
		return Color.parseColor("#007229");
	}

	public int getDistrictForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getDLRBackground() {
		return Color.parseColor("#00BBB4");
	}

	public int getDLRForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getHammersmithAndCityBackground() {
		return Color.parseColor("#E899A8");
	}

	public int getHammersmithAndCityForeground() {
		return Color.parseColor("#113892");
	}

	public int getJubileeBackground() {
		return Color.parseColor("#686E72");
	}

	public int getJubileeForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getMetropolitanBackground() {
		return Color.parseColor("#893267");
	}

	public int getMetropolitanForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getNorthernBackground() {
		return Color.parseColor("#000000");
	}

	public int getNorthernForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getOvergroundBackground() {
		return Color.parseColor("#F86C00"); // from image, text says #F86
	}

	public int getOvergroundForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getPiccadillyBackground() {
		return Color.parseColor("#113892");
	}

	public int getPiccadillyForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getVictoriaBackground() {
		return Color.parseColor("#009FE0");
	}

	public int getVictoriaForeground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getWaterlooAndCityBackground() {
		return Color.parseColor("#70C3C3");
	}

	public int getWaterlooAndCityForeground() {
		return Color.parseColor("#113892");
	}

	public int getUnknownBackground() {
		return Color.parseColor("#FFFFFF");
	}

	public int getUnknownForeground() {
		return Color.parseColor("#000000");
	}
}
