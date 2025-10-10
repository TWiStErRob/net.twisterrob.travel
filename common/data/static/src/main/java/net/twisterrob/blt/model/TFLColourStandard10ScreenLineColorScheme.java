package net.twisterrob.blt.model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TFLColourStandard10ScreenLineColorScheme extends LineColorScheme {

	/**
	 * https://tfl.gov.uk/info-for/business-and-advertisers/design-standards#tfl-corporate-design-standards-heading
	 */
	@Override public String getLineColorsSource() {
		return "https://content.tfl.gov.uk/tfl-colour-standard.pdf";
	}

	@Override public String getLineColorsName() {
		return "TfL Colour Standard";
	}

	@Override public String getLineColorsVersion() {
		return "Issue 10 / RGB";
	}

	@Override public Date getLineColorsDate() {
		return new GregorianCalendar(2025, Calendar.MAY, 0x00).getTime();
	}

	@Override public int getBakerlooBackground() {
		return makeColor(178, 99, 0);
	}

	@Override public int getCentralBackground() {
		return makeColor(220, 36, 31);
	}

	@Override public int getCircleBackground() {
		return makeColor(255, 200, 10);
	}

	@Override public int getDistrictBackground() {
		return makeColor(0, 125, 50);
	}

	@Override public int getHammersmithAndCityBackground() {
		return makeColor(245, 137, 166);
	}

	@Override public int getJubileeBackground() {
		return makeColor(131, 141, 147);
	}

	@Override public int getMetropolitanBackground() {
		return makeColor(155, 0, 88);
	}

	@Override public int getNorthernBackground() {
		return makeColor(0, 0, 0);
	}

	@Override public int getPiccadillyBackground() {
		return makeColor(0, 25, 168);
	}

	@Override public int getVictoriaBackground() {
		return makeColor(3, 155, 229);
	}

	@Override public int getWaterlooAndCityBackground() {
		return makeColor(118, 208, 189);
	}

	@Override public int getDLRBackground() {
		return makeColor(0, 175, 173);
	}

	@Override public int getOvergroundBackground() {
		return makeColor(250, 123, 5);
	}

	@Override public int getLibertyBackground() {
		return makeColor(93, 96, 97);
	}

	@Override public int getLionessBackground() {
		return makeColor(250, 166, 26);
	}

	@Override public int getMildmayBackground() {
		return makeColor(0, 119, 173);
	}

	@Override public int getSuffragetteBackground() {
		return makeColor(91, 189, 114);
	}

	@Override public int getWeaverBackground() {
		return makeColor(130, 58, 98);
	}

	@Override public int getWindrushBackground() {
		return makeColor(237, 27, 0);
	}

	@Override public int getTramBackground() {
		return makeColor(95, 181, 38);
	}

	@Override public int getEmiratesBackground() {
		return makeColor(115, 79, 160);
	}

	@Override public int getElizabethLineBackground() {
		return makeColor(96, 57, 158);
	}
}
