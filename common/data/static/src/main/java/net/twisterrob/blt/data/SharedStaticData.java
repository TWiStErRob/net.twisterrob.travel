package net.twisterrob.blt.data;

import net.twisterrob.blt.model.*;

public class SharedStaticData implements StaticData {
	private LineColors colors = new TubeStatusPresentationLineColors();

	public LineColors getLineColors() {
		return colors;
	}
}
