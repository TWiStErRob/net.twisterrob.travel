package net.twisterrob.blt.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TFLColourStandard3ScreenLineColorScheme extends LineColorScheme {
	@Override public String getLineColorsSource() {
		return "http://www.tfl.gov.uk/assets/downloads/corporate/tfl-colour-standard-issue03.pdf";
	}
	@Override public String getLineColorsName() {
		return "TfL Colour Standard";
	}
	@Override public String getLineColorsVersion() {
		return "Issue 3 / RGB for screen";
	}
	@Override public Date getLineColorsDate() {
		return new GregorianCalendar(2009, Calendar.FEBRUARY, 0x00).getTime();
	}

	@Override public int getBakerlooBackground() {
		return makeColor(137, 78, 36);
	}

	@Override public int getCentralBackground() {
		return makeColor(220, 36, 31);
	}

	@Override public int getCircleBackground() {
		return makeColor(255, 206, 0);
	}

	@Override public int getDistrictBackground() {
		return makeColor(0, 114, 41);
	}

	@Override public int getHammersmithAndCityBackground() {
		return makeColor(215, 153, 175);
	}

	@Override public int getJubileeBackground() {
		return makeColor(134, 143, 152);
	}

	@Override public int getMetropolitanBackground() {
		return makeColor(117, 16, 86);
	}

	@Override public int getNorthernBackground() {
		return makeColor(0, 0, 0);
	}

	@Override public int getPiccadillyBackground() {
		return makeColor(0, 25, 168);
	}

	@Override public int getVictoriaBackground() {
		return makeColor(0, 160, 226);
	}

	@Override public int getWaterlooAndCityBackground() {
		return makeColor(118, 208, 189);
	}

	@Override public int getDLRBackground() {
		return makeColor(0, 175, 173);
	}

	@Deprecated
	@Override public int getOvergroundBackground() {
		return makeColor(232, 106, 16);
	}

	@Override public int getTramBackground() {
		return 0xFF7ac141; // TODO real value
	}

	@Override public int getEmiratesBackground() {
		return 0xFFe91e3d; // TODO real value
	}
}
