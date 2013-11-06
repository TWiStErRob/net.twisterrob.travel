package net.twisterrob.blt.data.statics;

import java.util.*;

import net.twisterrob.blt.model.Line;

public class PredictionSummaryFilenames implements StaticDataFragment<Map<Line, String>> {
	public Map<Line, String> init() {
		Map<Line, String> files = new HashMap<>();
		files.put(Line.Bakerloo, "PredictionSummary-B.xml");
		files.put(Line.Central, "PredictionSummary-C.xml");
		files.put(Line.Circle, "PredictionSummary-H.xml");
		files.put(Line.District, "PredictionSummary-D.xml");
		files.put(Line.HammersmithAndCity, "PredictionSummary-H.xml");
		files.put(Line.Jubilee, "PredictionSummary-J.xml");
		files.put(Line.Metropolitan, "PredictionSummary-M.xml");
		files.put(Line.Northern, "PredictionSummary-N.xml");
		files.put(Line.Piccadilly, "PredictionSummary-P.xml");
		files.put(Line.Victoria, "PredictionSummary-V.xml");
		files.put(Line.WaterlooAndCity, "PredictionSummary-W.xml");
		return files;
	}
}
