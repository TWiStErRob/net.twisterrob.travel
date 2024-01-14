package net.twisterrob.blt.data;

import net.twisterrob.blt.model.*;

public class SharedStaticData implements StaticData {
	private LineColorScheme colors = new TubeStatusPresentationLineColorScheme();

	public LineColorScheme getLineColors() {
		return colors;
	}
}
