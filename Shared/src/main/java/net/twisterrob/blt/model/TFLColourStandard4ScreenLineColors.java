package net.twisterrob.blt.model;

import java.util.*;

public class TFLColourStandard4ScreenLineColors extends LineColors {

	/**
	 * https://tfl.gov.uk/info-for/suppliers-and-contractors/design-standards#tfl-corporate-design-standards-heading
	 */
	@Override public String getLineColorsSource() {
		// https://tfl.gov.uk/cdn/static/cms/documents/tfl-colour-standards-issue04.pdf
		return "http://content.tfl.gov.uk/tfl-colour-standards-issue04.pdf";
	}
	@Override public String getLineColorsName() {
		return "TfL Colour Standard";
	}
	@Override public String getLineColorsVersion() {
		return "Issue 4 / RGB for screen";
	}
	@Override public Date getLineColorsDate() {
		return new GregorianCalendar(2016, Calendar.MAY, 0x00).getTime();
	}

	@Override public int getBakerlooBackground() {
		return makeColor(178, 99, 0);
	}

	@Override public int getCentralBackground() {
		return makeColor(220, 36, 31);
	}

	@Override public int getCircleBackground() {
		return makeColor(255, 211, 41);
	}

	@Override public int getDistrictBackground() {
		return makeColor(0, 125, 50);
	}

	@Override public int getHammersmithAndCityBackground() {
		return makeColor(244, 169, 190);
	}

	@Override public int getJubileeBackground() {
		return makeColor(161, 165, 167);
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
		return makeColor(0, 152, 216);
	}

	@Override public int getWaterlooAndCityBackground() {
		return makeColor(147, 206, 186);
	}

	@Override public int getDLRBackground() {
		return makeColor(0, 175, 173);
	}

	@Override public int getOvergroundBackground() {
		return makeColor(239, 123, 16);
	}

	@Override public int getTramBackground() {
		return makeColor(0, 189, 25);
	}

	@Override public int getEmiratesBackground() {
		return makeColor(220, 36, 31);
	}

	@Deprecated
	@Override public int getTfLRailBackground() {
		return makeColor(0, 25, 168);
	}

	@Override public int getElizabethLineBackground() {
		return makeColor(147, 100, 204);
	}
}
