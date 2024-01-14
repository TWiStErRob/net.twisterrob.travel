package net.twisterrob.blt.data;

import net.twisterrob.blt.model.LineColorScheme;
import net.twisterrob.blt.model.TubeStatusPresentationLineColorScheme;

public class SharedStaticData implements StaticData {
	private LineColorScheme colors = new TubeStatusPresentationLineColorScheme();

	public LineColorScheme getLineColors() {
		return colors;
	}
}
