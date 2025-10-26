package net.twisterrob.blt.model;

import java.util.Date;

/**
 * Assume a light default background when subclassing this.
 */
public abstract class LineColorScheme {
	abstract String getLineColorsSource();
	abstract String getLineColorsName();
	abstract String getLineColorsVersion();
	abstract Date getLineColorsDate();

	public int getBakerlooBackground() {
		return getUnknownBackground();
	}
	public int getBakerlooForeground() {
		return getUnknownForeground();
	}

	public int getCentralBackground() {
		return getUnknownBackground();
	}
	public int getCentralForeground() {
		return getUnknownForeground();
	}

	public int getCircleBackground() {
		return getUnknownBackground();
	}
	public int getCircleForeground() {
		return getUnknownForeground();
	}

	public int getDistrictBackground() {
		return getUnknownBackground();
	}
	public int getDistrictForeground() {
		return getUnknownForeground();
	}

	public int getHammersmithAndCityBackground() {
		return getUnknownBackground();
	}
	public int getHammersmithAndCityForeground() {
		return getUnknownForeground();
	}

	public int getJubileeBackground() {
		return getUnknownBackground();
	}
	public int getJubileeForeground() {
		return getUnknownForeground();
	}

	public int getMetropolitanBackground() {
		return getUnknownBackground();
	}
	public int getMetropolitanForeground() {
		return getUnknownForeground();
	}

	public int getNorthernBackground() {
		return getUnknownBackground();
	}
	public int getNorthernForeground() {
		return getUnknownForeground();
	}

	public int getPiccadillyBackground() {
		return getUnknownBackground();
	}
	public int getPiccadillyForeground() {
		return getUnknownForeground();
	}

	public int getVictoriaBackground() {
		return getUnknownBackground();
	}
	public int getVictoriaForeground() {
		return getUnknownForeground();
	}

	public int getWaterlooAndCityBackground() {
		return getUnknownBackground();
	}
	public int getWaterlooAndCityForeground() {
		return getUnknownForeground();
	}

	public int getDLRBackground() {
		return getUnknownBackground();
	}
	public int getDLRForeground() {
		return getUnknownForeground();
	}

	/**
	 * @deprecated Use one of
	 * {@link #getLibertyBackground()},
	 * {@link #getLionessBackground()},
	 * {@link #getMildmayBackground()},
	 * {@link #getSuffragetteBackground()},
	 * {@link #getWeaverBackground()},
	 * {@link #getWindrushBackground()}
	 * instead.
	 */
	@Deprecated
	public int getOvergroundBackground() {
		return getUnknownBackground();
	}
	/**
	 * @deprecated Use one of
	 * {@link #getLibertyForeground()},
	 * {@link #getLionessForeground()},
	 * {@link #getMildmayForeground()},
	 * {@link #getSuffragetteForeground()},
	 * {@link #getWeaverForeground()},
	 * {@link #getWindrushForeground()}
	 * instead.
	 */
	@Deprecated
	public int getOvergroundForeground() {
		return getUnknownForeground();
	}

	public int getLibertyBackground() {
		return getUnknownBackground();
	}
	public int getLibertyForeground() {
		return getUnknownForeground();
	}

	public int getLionessBackground() {
		return getUnknownBackground();
	}
	public int getLionessForeground() {
		return getUnknownForeground();
	}

	public int getMildmayBackground() {
		return getUnknownBackground();
	}
	public int getMildmayForeground() {
		return getUnknownForeground();
	}

	public int getSuffragetteBackground() {
		return getUnknownBackground();
	}
	public int getSuffragetteForeground() {
		return getUnknownForeground();
	}

	public int getWeaverBackground() {
		return getUnknownBackground();
	}
	public int getWeaverForeground() {
		return getUnknownForeground();
	}

	public int getWindrushBackground() {
		return getUnknownBackground();
	}
	public int getWindrushForeground() {
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

	/**
	 * @deprecated Use {@link #getElizabethLineBackground()} instead.
	 */
	@Deprecated
	public int getTfLRailBackground() {
		return getUnknownBackground();
	}
	/**
	 * @deprecated Use {@link #getElizabethLineForeground()} instead.
	 */
	@Deprecated
	public int getTfLRailForeground() {
		return getUnknownForeground();
	}

	public int getElizabethLineBackground() {
		return getUnknownBackground();
	}
	public int getElizabethLineForeground() {
		return getUnknownForeground();
	}

	public int getUnknownBackground() {
		return makeColor(255, 255, 255);
	}
	public int getUnknownForeground() {
		return makeColor(0, 0, 0);
	}

	protected int makeColor(int red, int green, int blue) {
		final int alpha = 0xFF;
		//noinspection NumericOverflow int is treated as unsigned
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
}
