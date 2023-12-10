package net.twisterrob.blt.android.data.range;

import android.graphics.Point;

import net.twisterrob.android.utils.tools.AndroidTools;

public class AndroidOpenGLRenderedGeoSize extends RenderedGeoSize {
	@Override protected MaxSize getSafeMaximum() {
		Point size = AndroidTools.getMaximumBitmapSize(null);
		MaxSize max = new MaxSize();
		max.width = size.x;
		max.height = size.y;
		return max;
	}
}
