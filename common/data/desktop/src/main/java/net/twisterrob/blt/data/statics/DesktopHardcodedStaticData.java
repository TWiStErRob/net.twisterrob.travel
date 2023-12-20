package net.twisterrob.blt.data.statics;

import java.io.File;
import java.util.*;

import static java.util.Collections.*;

import net.twisterrob.blt.data.SharedStaticData;
import net.twisterrob.blt.data.apps.DesktopStaticData;
import net.twisterrob.blt.model.Line;

public class DesktopHardcodedStaticData extends SharedStaticData implements DesktopStaticData {
	private static final String TIMETABLE_ROOT = "../temp/feed15/LULDLRRiverTramCable"; // STOPSHIP
	private static final String DATA_ROOT = "../data/PredictionSummary/"; // STOPSHIP
	private static final File OUTPUT_FOLDER = new File("output");

	private final Map<Line, List<String>> m_timetableFilenames = new TimetableFilenames2016().init();
	private final Map<Line, String> m_predictionSummaryFilenames = new PredictionSummaryFilenames().init();
	private final String timetableRoot;
	private final File outputDir;

	@Deprecated // STOPSHIP remove
	public DesktopHardcodedStaticData() {
		timetableRoot = TIMETABLE_ROOT;
		outputDir = OUTPUT_FOLDER;
	}
	public DesktopHardcodedStaticData(String timetableRoot, File outputDir) {
		this.timetableRoot = timetableRoot;
		this.outputDir = outputDir;
	}

	public String getTimetableRoot() {
		return timetableRoot;
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

	@Deprecated // STOPSHIP remove
	@Override public File getOut(String path) {
		return new File(outputDir, path);
	}
}
