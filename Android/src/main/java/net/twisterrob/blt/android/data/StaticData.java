package net.twisterrob.blt.android.data;

import java.util.Map;

import net.twisterrob.blt.model.*;

public interface StaticData {
	Map<StopType, Integer> getStopTypeLogos();
	LineColors getLineColors();
}
