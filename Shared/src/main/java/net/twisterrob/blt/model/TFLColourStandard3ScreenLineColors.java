package net.twisterrob.blt.model;

import java.util.*;

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
		return new GregorianCalendar(2009, 1, 0x00).getTime();
	}

	public int getBakerlooBackground() {
		return 0xFF894E24; // 137, 78, 36
	}

	public int getBakerlooForeground() {
		return getUnknownForeground();
	}

	public int getCentralBackground() {
		return 0xFFDC241F; // 220, 36, 31
	}

	public int getCentralForeground() {
		return getUnknownForeground();
	}

	public int getCircleBackground() {
		return 0xFFFFCE00; // 255, 206, 0
	}

	public int getCircleForeground() {
		return getUnknownForeground();
	}

	public int getDistrictBackground() {
		return 0xFF007229; // 0, 114, 41
	}

	public int getDistrictForeground() {
		return getUnknownForeground();
	}

	public int getHammersmithAndCityBackground() {
		return 0xFFD799AF; // 215, 153, 175
	}

	public int getHammersmithAndCityForeground() {
		return getUnknownForeground();
	}

	public int getJubileeBackground() {
		return 0xFF868F98; // 134, 143, 152
	}

	public int getJubileeForeground() {
		return getUnknownForeground();
	}

	public int getMetropolitanBackground() {
		return 0xFF751056; // 117, 16, 86
	}

	public int getMetropolitanForeground() {
		return getUnknownForeground();
	}

	public int getNorthernBackground() {
		return 0xFF000000; // 0, 0, 0
	}

	public int getNorthernForeground() {
		return getUnknownForeground();
	}

	public int getPiccadillyBackground() {
		return 0xFF0019A8; // 0, 25, 168
	}

	public int getPiccadillyForeground() {
		return getUnknownForeground();
	}

	public int getVictoriaBackground() {
		return 0xFF00A0E2; // 0, 160, 226
	}

	public int getVictoriaForeground() {
		return getUnknownForeground();
	}

	public int getWaterlooAndCityBackground() {
		return 0xFF76D0BD; // 118, 208, 189
	}

	public int getWaterlooAndCityForeground() {
		return getUnknownForeground();
	}

	public int getDLRBackground() {
		return 0xFF00AFAD; // 0, 175, 173
	}

	public int getDLRForeground() {
		return getUnknownForeground();
	}

	public int getOvergroundBackground() {
		return 0xFFE86A10; // 232, 106, 16
	}

	public int getOvergroundForeground() {
		return getUnknownForeground();
	}

	public int getTramBackground() {
		return 0xFF7ac141; // TODO real value
	}

	public int getTramForeground() {
		return getUnknownForeground();
	}

	public int getEmiratesBackground() {
		return 0xFFe91e3d; // TODO real value
	}

	public int getEmiratesForeground() {
		return getUnknownForeground();
	}

	public int getUnknownBackground() {
		return 0xFFFFFFFF; // 255, 255, 255
	}

	public int getUnknownForeground() {
		return 0xFF000000; // 0, 0, 0
	}
}
