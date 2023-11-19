package org.maptiler;

import java.util.Locale;

/** Pixels in pyramid, XY pixels Z zoom */
public class Pixels {
	public final long x, y;
	public Pixels(long x, long y) {
		this.x = x;
		this.y = y;
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "(%s, %s)", x, y);
	}
}
