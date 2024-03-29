package net.twisterrob.blt.android.data;

import java.util.Map;

import net.twisterrob.blt.model.LineColorScheme;
import net.twisterrob.blt.model.StopType;

public interface AndroidStaticData {
	Map<StopType, Integer> getStopTypeLogos();
	Map<StopType, Integer> getStopTypeMapIcons();
	Map<StopType, Integer> getStopTypeMiniIcons();
	LineColorScheme getLineColors();
}
