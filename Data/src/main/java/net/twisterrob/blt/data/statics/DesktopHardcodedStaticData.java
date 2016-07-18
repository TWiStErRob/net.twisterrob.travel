package net.twisterrob.blt.data.statics;

import static java.util.Collections.*;

import java.io.File;
import java.util.*;

import net.twisterrob.blt.data.SharedStaticData;
import net.twisterrob.blt.data.apps.DesktopStaticData;
import net.twisterrob.blt.model.Line;

public class DesktopHardcodedStaticData extends SharedStaticData implements DesktopStaticData {
	private static final String TIMETABLE_ROOT = "../temp/feed15/lultramdlrcablecarriver";
	private static final String DATA_ROOT = "./src/data/PredictionSummary/";
	private static final File OUTPUT_FOLDER = new File("output");

	private final Map<Line, List<String>> m_timetableFilenames = new TimetableFilenames().init();
	private final Map<Line, String> m_predictionSummaryFilenames = new PredictionSummaryFilenames().init();

	public String getTimetableRoot() {
		return TIMETABLE_ROOT;
	}
	public Map<Line, List<String>> getTimetableFilenames() {
		return unmodifiableMap(m_timetableFilenames);
	}

	public String getPredictionSummaryRoot() {
		return DATA_ROOT;
	}

	public Map<Line, String> getPredictionSummaryFilenames() {
		return unmodifiableMap(m_predictionSummaryFilenames);
	}
	@Override public File getOut(String path) {
		return new File(OUTPUT_FOLDER, path);
	}
}
