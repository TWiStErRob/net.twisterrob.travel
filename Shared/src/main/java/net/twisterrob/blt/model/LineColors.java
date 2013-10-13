package net.twisterrob.blt.model;

import java.util.Date;

public interface LineColors {
	String getLineColorsSource();
	String getLineColorsName();
	String getLineColorsVersion();
	Date getLineColorsDate();

	int getBakerlooBackground();
	int getBakerlooForeground();

	int getCentralBackground();
	int getCentralForeground();

	int getCircleBackground();
	int getCircleForeground();

	int getDistrictBackground();
	int getDistrictForeground();

	int getHammersmithAndCityBackground();
	int getHammersmithAndCityForeground();

	int getJubileeBackground();
	int getJubileeForeground();

	int getMetropolitanBackground();
	int getMetropolitanForeground();

	int getNorthernBackground();
	int getNorthernForeground();

	int getPiccadillyBackground();
	int getPiccadillyForeground();

	int getVictoriaBackground();
	int getVictoriaForeground();

	int getWaterlooAndCityBackground();
	int getWaterlooAndCityForeground();

	int getDLRBackground();
	int getDLRForeground();

	int getOvergroundBackground();
	int getOvergroundForeground();

	int getTramBackground();
	int getTramForeground();

	int getEmiratesBackground();
	int getEmiratesForeground();

	int getUnknownBackground();
	int getUnknownForeground();
}
