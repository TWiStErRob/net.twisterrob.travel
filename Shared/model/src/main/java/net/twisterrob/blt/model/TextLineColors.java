package net.twisterrob.blt.model;

import java.util.Date;

public class TextLineColors extends LineColors {
	private final LineColors colors;
	private final int background = 0x00000000;
	public TextLineColors(LineColors colors) {
		this.colors = colors;
	}
	@Override public String getLineColorsSource() {
		return colors.getLineColorsSource();
	}
	@Override public String getLineColorsName() {
		return colors.getLineColorsName();
	}
	@Override public String getLineColorsVersion() {
		return colors.getLineColorsVersion();
	}
	@Override public Date getLineColorsDate() {
		return colors.getLineColorsDate();
	}
	@Override public int getBakerlooBackground() {
		return background;
	}
	@Override public int getBakerlooForeground() {
		return colors.getBakerlooBackground();
	}
	@Override public int getCentralBackground() {
		return background;
	}
	@Override public int getCentralForeground() {
		return colors.getCentralBackground();
	}
	@Override public int getCircleBackground() {
		return background;
	}
	@Override public int getCircleForeground() {
		return colors.getCircleBackground();
	}
	@Override public int getDistrictBackground() {
		return background;
	}
	@Override public int getDistrictForeground() {
		return colors.getDistrictBackground();
	}
	@Override public int getHammersmithAndCityBackground() {
		return background;
	}
	@Override public int getHammersmithAndCityForeground() {
		return colors.getHammersmithAndCityBackground();
	}
	@Override public int getJubileeBackground() {
		return background;
	}
	@Override public int getJubileeForeground() {
		return colors.getJubileeBackground();
	}
	@Override public int getMetropolitanBackground() {
		return background;
	}
	@Override public int getMetropolitanForeground() {
		return colors.getMetropolitanBackground();
	}
	@Override public int getNorthernBackground() {
		return background;
	}
	@Override public int getNorthernForeground() {
		return colors.getNorthernBackground();
	}
	@Override public int getPiccadillyBackground() {
		return background;
	}
	@Override public int getPiccadillyForeground() {
		return colors.getPiccadillyBackground();
	}
	@Override public int getVictoriaBackground() {
		return background;
	}
	@Override public int getVictoriaForeground() {
		return colors.getVictoriaBackground();
	}
	@Override public int getWaterlooAndCityBackground() {
		return background;
	}
	@Override public int getWaterlooAndCityForeground() {
		return colors.getWaterlooAndCityBackground();
	}
	@Override public int getDLRBackground() {
		return background;
	}
	@Override public int getDLRForeground() {
		return colors.getDLRBackground();
	}
	@Override public int getOvergroundBackground() {
		return background;
	}
	@Override public int getOvergroundForeground() {
		return colors.getOvergroundBackground();
	}
	@Override public int getTramBackground() {
		return background;
	}
	@Override public int getTramForeground() {
		return colors.getTramBackground();
	}
	@Override public int getEmiratesBackground() {
		return background;
	}
	@Override public int getEmiratesForeground() {
		return colors.getEmiratesBackground();
	}
	@Deprecated
	@Override public int getTfLRailBackground() {
		return background;
	}
	@Deprecated
	@Override public int getTfLRailForeground() {
		return colors.getTfLRailBackground();
	}
	@Override public int getElizabethLineBackground() {
		return background;
	}
	@Override public int getElizabethLineForeground() {
		return colors.getElizabethLineBackground();
	}
	@Override public int getUnknownBackground() {
		return background;
	}
	@Override public int getUnknownForeground() {
		return colors.getUnknownBackground();
	}
}
