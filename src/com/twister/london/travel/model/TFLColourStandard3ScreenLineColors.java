package com.twister.london.travel.model;

import java.util.*;

import android.graphics.Color;

public class TFLColourStandard3ScreenLineColors implements LineColors {
	public String getLineColorsSource() {
		return "http://www.tfl.gov.uk/assets/downloads/corporate/tfl-colour-standard-issue03.pdf";
	}
	public String getLineColorsName() {
		return "TfL Colour Standard";
	}
	public String getLineColorsVersion() {
		return "Issue 3 / RGB for screen";
	}
	public Date getLineColorsDate() {
		return new GregorianCalendar(2009, 1, 0).getTime();
	}

	public int getBakerlooBackground() {
		return Color.rgb(137, 78, 36);
	}

	public int getBakerlooForeground() {
		return getUnknownForeground();
	}

	public int getCentralBackground() {
		return Color.rgb(220, 36, 31);
	}

	public int getCentralForeground() {
		return getUnknownForeground();
	}

	public int getCircleBackground() {
		return Color.rgb(255, 206, 0);
	}

	public int getCircleForeground() {
		return getUnknownForeground();
	}

	public int getDistrictBackground() {
		return Color.rgb(0, 114, 41);
	}

	public int getDistrictForeground() {
		return getUnknownForeground();
	}

	public int getDLRBackground() {
		return Color.rgb(0, 175, 173);
	}

	public int getDLRForeground() {
		return getUnknownForeground();
	}

	public int getHammersmithAndCityBackground() {
		return Color.rgb(215, 153, 175);
	}

	public int getHammersmithAndCityForeground() {
		return getUnknownForeground();
	}

	public int getJubileeBackground() {
		return Color.rgb(134, 143, 152);
	}

	public int getJubileeForeground() {
		return getUnknownForeground();
	}

	public int getMetropolitanBackground() {
		return Color.rgb(117, 16, 86);
	}

	public int getMetropolitanForeground() {
		return getUnknownForeground();
	}

	public int getNorthernBackground() {
		return Color.rgb(0, 0, 0);
	}

	public int getNorthernForeground() {
		return getUnknownForeground();
	}

	public int getOvergroundBackground() {
		return Color.rgb(232, 106, 16);
	}

	public int getOvergroundForeground() {
		return getUnknownForeground();
	}

	public int getPiccadillyBackground() {
		return Color.rgb(0, 25, 168);
	}

	public int getPiccadillyForeground() {
		return getUnknownForeground();
	}

	public int getVictoriaBackground() {
		return Color.rgb(0, 160, 226);
	}

	public int getVictoriaForeground() {
		return getUnknownForeground();
	}

	public int getWaterlooAndCityBackground() {
		return Color.rgb(118, 208, 189);
	}

	public int getWaterlooAndCityForeground() {
		return getUnknownForeground();
	}

	public int getUnknownBackground() {
		return Color.rgb(255, 255, 255);
	}

	public int getUnknownForeground() {
		return Color.rgb(0, 0, 0);
	}
}
