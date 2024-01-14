package net.twisterrob.blt.model;

import javax.annotation.Nonnull;

public class LineColors {

	private final @Nonnull LineColorScheme colors;

	public LineColors(@Nonnull LineColorScheme colors) {
		this.colors = colors;
	}

	@SuppressWarnings("deprecation")
	public int getBackground(@Nonnull Line line) {
		switch (line) {
			case Bakerloo:
				return colors.getBakerlooBackground();
			case Central:
				return colors.getCentralBackground();
			case Circle:
				return colors.getCircleBackground();
			case District:
				return colors.getDistrictBackground();
			case HammersmithAndCity:
				return colors.getHammersmithAndCityBackground();
			case Jubilee:
				return colors.getJubileeBackground();
			case Metropolitan:
				return colors.getMetropolitanBackground();
			case Northern:
				return colors.getNorthernBackground();
			case Piccadilly:
				return colors.getPiccadillyBackground();
			case Victoria:
				return colors.getVictoriaBackground();
			case WaterlooAndCity:
				return colors.getWaterlooAndCityBackground();
			case DLR:
				return colors.getDLRBackground();
			case Overground:
				return colors.getOvergroundBackground();
			case ElizabethLine:
				return colors.getElizabethLineBackground();
			case EmiratesAirline:
				return colors.getEmiratesBackground();
			case Tram:
				return colors.getTramBackground();
			case TflRail:
				return colors.getTfLRailBackground();
			default:
			case unknown:
				return colors.getUnknownBackground();
		}
	}
	
	@SuppressWarnings("deprecation")
	public int getForeground(@Nonnull Line line) {
		switch (line) {
			case Bakerloo:
				return colors.getBakerlooForeground();
			case Central:
				return colors.getCentralForeground();
			case Circle:
				return colors.getCircleForeground();
			case District:
				return colors.getDistrictForeground();
			case HammersmithAndCity:
				return colors.getHammersmithAndCityForeground();
			case Jubilee:
				return colors.getJubileeForeground();
			case Metropolitan:
				return colors.getMetropolitanForeground();
			case Northern:
				return colors.getNorthernForeground();
			case Piccadilly:
				return colors.getPiccadillyForeground();
			case Victoria:
				return colors.getVictoriaForeground();
			case WaterlooAndCity:
				return colors.getWaterlooAndCityForeground();
			case DLR:
				return colors.getDLRForeground();
			case Overground:
				return colors.getOvergroundForeground();
			case ElizabethLine:
				return colors.getElizabethLineForeground();
			case EmiratesAirline:
				return colors.getEmiratesForeground();
			case Tram:
				return colors.getTramForeground();
			case TflRail:
				return colors.getTfLRailForeground();
			default:
			case unknown:
				return colors.getUnknownForeground();
		}
	}

}
