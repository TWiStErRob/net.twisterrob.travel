package net.twisterrob.blt.data;

import net.twisterrob.blt.model.LineColorScheme;
import net.twisterrob.blt.model.TFLColourStandard10ScreenLineColorScheme;

public class SharedStaticData implements StaticData {
	private LineColorScheme colors = new TFLColourStandard10ScreenLineColorScheme();

	public LineColorScheme getLineColors() {
		return colors;
	}
}
