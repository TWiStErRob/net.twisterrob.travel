package com.twister.london.travel.model;

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

	int getDLRBackground();
	int getDLRForeground();

	int getHammersmithAndCityBackground();
	int getHammersmithAndCityForeground();

	int getJubileeBackground();
	int getJubileeForeground();

	int getMetropolitanBackground();
	int getMetropolitanForeground();

	int getNorthernBackground();
	int getNorthernForeground();

	int getOvergroundBackground();
	int getOvergroundForeground();

	int getPiccadillyBackground();
	int getPiccadillyForeground();

	int getVictoriaBackground();
	int getVictoriaForeground();

	int getWaterlooAndCityBackground();
	int getWaterlooAndCityForeground();

	int getUnknownBackground();
	int getUnknownForeground();
}
