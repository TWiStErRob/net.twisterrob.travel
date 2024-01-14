package net.twisterrob.blt.data.apps;

import java.util.List;
import java.util.Map;

import net.twisterrob.blt.data.StaticData;
import net.twisterrob.blt.model.Line;

public interface DesktopStaticData extends StaticData {
	String getTimetableRoot();
	Map<Line, List<String>> getTimetableFilenames();

	String getPredictionSummaryRoot();
	Map<Line, String> getPredictionSummaryFilenames();
}
