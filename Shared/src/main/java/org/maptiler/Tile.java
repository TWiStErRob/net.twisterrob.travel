package org.maptiler;

import java.util.Locale;

/** Tiles in pyramid, XYZ from TMS */
public class Tile {
	public final int x, y;
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "(%s, %s)", x, y);
	}
}
