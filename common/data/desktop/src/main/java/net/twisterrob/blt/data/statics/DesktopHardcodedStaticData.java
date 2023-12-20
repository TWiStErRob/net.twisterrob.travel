package net.twisterrob.blt.data.statics;

import java.io.File;
import java.util.*;

import static java.util.Collections.*;

import net.twisterrob.blt.data.SharedStaticData;
import net.twisterrob.blt.data.apps.DesktopStaticData;
import net.twisterrob.blt.model.Line;

public class DesktopHardcodedStaticData extends SharedStaticData implements DesktopStaticData {

	private final Map<Line, List<String>> m_timetableFilenames = new TimetableFilenames2016().init();
	private final Map<Line, String> m_predictionSummaryFilenames = new PredictionSummaryFilenames().init();
	private final File timetableRoot;
	private final File predictionRoot;

	public DesktopHardcodedStaticData(File timetableRoot, File predictionRoot) {
		this.timetableRoot = timetableRoot;
		this.predictionRoot = predictionRoot;
	}

	public String getTimetableRoot() {
		return timetableRoot.getAbsolutePath();
	}
	public Map<Line, List<String>> getTimetableFilenames() {
		return unmodifiableMap(m_timetableFilenames);
	}

	public String getPredictionSummaryRoot() {
		return predictionRoot.getAbsolutePath();
	}

	public Map<Line, String> getPredictionSummaryFilenames() {
		return unmodifiableMap(m_predictionSummaryFilenames);
	}
}
