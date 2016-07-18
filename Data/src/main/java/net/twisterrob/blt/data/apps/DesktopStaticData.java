package net.twisterrob.blt.data.apps;

import java.io.File;
import java.util.*;

import net.twisterrob.blt.data.StaticData;
import net.twisterrob.blt.data.statics.DesktopHardcodedStaticData;
import net.twisterrob.blt.model.Line;

public interface DesktopStaticData extends StaticData {
	String getTimetableRoot();
	Map<Line, List<String>> getTimetableFilenames();

	String getPredictionSummaryRoot();
	Map<Line, String> getPredictionSummaryFilenames();

	File getOut(String path);

	DesktopStaticData INSTANCE = new DesktopHardcodedStaticData();
}
