package net.twisterrob.blt.model;

import java.util.*;

public class TubeStatusPresentationLineColors extends LineColors {
	@Override public String getLineColorsSource() {
		return "http://content.tfl.gov.uk/tube-status-presentation-user-guide.pdf";
	}
	@Override public String getLineColorsName() {
		return "User Guide for the presentation of Service updates";
	}
	@Override public String getLineColorsVersion() {
		return "TfL Online User Guide Version 2";
	}
	@Override public Date getLineColorsDate() {
		// Not updated yet on 2022-11-07.
		return new GregorianCalendar(2012, Calendar.MARCH, 0).getTime();
	}

	@Override public int getBakerlooBackground() {
		return 0xFFAE6118;
	}

	@Override public int getBakerlooForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getCentralBackground() {
		return 0xFFE41F1F;
	}

	@Override public int getCentralForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getCircleBackground() {
		return 0xFFF8D42D;
	}

	@Override public int getCircleForeground() {
		return 0xFF113892;
	}

	@Override public int getDistrictBackground() {
		return 0xFF007229;
	}

	@Override public int getDistrictForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getHammersmithAndCityBackground() {
		return 0xFFE899A8;
	}

	@Override public int getHammersmithAndCityForeground() {
		return 0xFF113892;
	}

	@Override public int getJubileeBackground() {
		return 0xFF686E72;
	}

	@Override public int getJubileeForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getMetropolitanBackground() {
		return 0xFF893267;
	}

	@Override public int getMetropolitanForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getNorthernBackground() {
		return 0xFF000000;
	}

	@Override public int getNorthernForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getPiccadillyBackground() {
		return 0xFF113892;
	}

	@Override public int getPiccadillyForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getVictoriaBackground() {
		return 0xFF009FE0;
	}

	@Override public int getVictoriaForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getWaterlooAndCityBackground() {
		return 0xFF70C3C3;
	}

	@Override public int getWaterlooAndCityForeground() {
		return 0xFF113892;
	}

	@Override public int getDLRBackground() {
		return 0xFF00BBB4;
	}

	@Override public int getDLRForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getOvergroundBackground() {
		return 0xFFF86C00; // from image, text says #F86
	}

	@Override public int getOvergroundForeground() {
		return 0xFFFFFFFF;
	}

	@Override public int getTramBackground() {
		return 0xFF7ac141; // TODO real value
	}

	@Override public int getTramForeground() {
		return 0xFF000000;
	}

	@Override public int getEmiratesBackground() {
		return 0xFFe91e3d; // TODO real value
	}

	@Override public int getEmiratesForeground() {
		return 0xFF000000;
	}

	/**
	 * Header of TfL Rail timetables:
	 * http://content.tfl.gov.uk/tfl-rail-liverpool-street-shenfield-timetable-15-dec-2019-to-16-may-2020.pdf
	 */
	@Deprecated
	@Override public int getTfLRailBackground() {
		return 0xFF243588; // TODO real value
	}

	@Deprecated
	@Override public int getTfLRailForeground() {
		return 0xFFFFFFFF;
	}

	/**
	 * Header of Elizabeth line map:
	 * http://content.tfl.gov.uk/elizabeth-line-when-fully-open.pdf
	 */
	@Override public int getElizabethLineBackground() {
		return 0xFF603e99; // TODO real value
	}

	@Override public int getElizabethLineForeground() {
		return 0xFFFFFFFF;
	}
}
