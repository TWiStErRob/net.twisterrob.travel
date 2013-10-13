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
		return 0x00894E24; // 137, 78, 36
	}

	public int getBakerlooForeground() {
		return getUnknownForeground();
	}

	public int getCentralBackground() {
		return 0x00DC241F; // 220, 36, 31
	}

	public int getCentralForeground() {
		return getUnknownForeground();
	}

	public int getCircleBackground() {
		return 0x00FFCE00; // 255, 206, 0
	}

	public int getCircleForeground() {
		return getUnknownForeground();
	}

	public int getDistrictBackground() {
		return 0x00007229; // 0, 114, 41
	}

	public int getDistrictForeground() {
		return getUnknownForeground();
	}

	public int getHammersmithAndCityBackground() {
		return 0x00D799AF; // 215, 153, 175
	}

	public int getHammersmithAndCityForeground() {
		return getUnknownForeground();
	}

	public int getJubileeBackground() {
		return 0x00868F98; // 134, 143, 152
	}

	public int getJubileeForeground() {
		return getUnknownForeground();
	}

	public int getMetropolitanBackground() {
		return 0x00751056; // 117, 16, 86
	}

	public int getMetropolitanForeground() {
		return getUnknownForeground();
	}

	public int getNorthernBackground() {
		return 0x00000000; // 0, 0, 0
	}

	public int getNorthernForeground() {
		return getUnknownForeground();
	}

	public int getPiccadillyBackground() {
		return 0x000019A8; // 0, 25, 168
	}

	public int getPiccadillyForeground() {
		return getUnknownForeground();
	}

	public int getVictoriaBackground() {
		return 0x0000A0E2; // 0, 160, 226
	}

	public int getVictoriaForeground() {
		return getUnknownForeground();
	}

	public int getWaterlooAndCityBackground() {
		return 0x0076D0BD; // 118, 208, 189
	}

	public int getWaterlooAndCityForeground() {
		return getUnknownForeground();
	}

	public int getDLRBackground() {
		return 0x0000AFAD; // 0, 175, 173
	}

	public int getDLRForeground() {
		return getUnknownForeground();
	}

	public int getOvergroundBackground() {
		return 0x00E86A10; // 232, 106, 16
	}

	public int getOvergroundForeground() {
		return getUnknownForeground();
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
		return 0x00FFFFFF; // 255, 255, 255
	}

	public int getUnknownForeground() {
		return 0x00000000; // 0, 0, 0
	}
}
