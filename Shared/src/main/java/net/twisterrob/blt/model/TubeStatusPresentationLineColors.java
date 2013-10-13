package net.twisterrob.blt.model;

import java.util.*;

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
		return 0xFFAE6118;
	}

	public int getBakerlooForeground() {
		return 0xFFFFFFFF;
	}

	public int getCentralBackground() {
		return 0xFFE41F1F;
	}

	public int getCentralForeground() {
		return 0xFFFFFFFF;
	}

	public int getCircleBackground() {
		return 0xFFF8D42D;
	}

	public int getCircleForeground() {
		return 0xFF113892;
	}

	public int getDistrictBackground() {
		return 0xFF007229;
	}

	public int getDistrictForeground() {
		return 0xFFFFFFFF;
	}

	public int getHammersmithAndCityBackground() {
		return 0xFFE899A8;
	}

	public int getHammersmithAndCityForeground() {
		return 0xFF113892;
	}

	public int getJubileeBackground() {
		return 0xFF686E72;
	}

	public int getJubileeForeground() {
		return 0xFFFFFFFF;
	}

	public int getMetropolitanBackground() {
		return 0xFF893267;
	}

	public int getMetropolitanForeground() {
		return 0xFFFFFFFF;
	}

	public int getNorthernBackground() {
		return 0xFF000000;
	}

	public int getNorthernForeground() {
		return 0xFFFFFFFF;
	}

	public int getPiccadillyBackground() {
		return 0xFF113892;
	}

	public int getPiccadillyForeground() {
		return 0xFFFFFFFF;
	}

	public int getVictoriaBackground() {
		return 0xFF009FE0;
	}

	public int getVictoriaForeground() {
		return 0xFFFFFFFF;
	}

	public int getWaterlooAndCityBackground() {
		return 0xFF70C3C3;
	}

	public int getWaterlooAndCityForeground() {
		return 0xFF113892;
	}

	public int getDLRBackground() {
		return 0xFF00BBB4;
	}

	public int getDLRForeground() {
		return 0xFFFFFFFF;
	}

	public int getOvergroundBackground() {
		return 0xFFF86C00; // from image, text says #F86
	}

	public int getOvergroundForeground() {
		return 0xFFFFFFFF;
	}

	public int getTramBackground() {
		return getUnknownBackground();
	}

	public int getTramForeground() {
		return getUnknownForeground();
	}

	public int getEmiratesBackground() {
		return getUnknownBackground();
	}

	public int getEmiratesForeground() {
		return getUnknownForeground();
	}

	public int getUnknownBackground() {
		return 0xFFFFFFFF;
	}

	public int getUnknownForeground() {
		return 0xFF000000;
	}
}
