package net.twisterrob.blt.data.statics;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import net.twisterrob.blt.model.Line;

public class TimetableFilenames2013 implements StaticDataFragment<Map<Line, List<String>>> {
	@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
	public Map<Line, List<String>> init() {
		Map<Line, List<String>> fileNames = new TreeMap<>();
		fileNames.put(Line.Bakerloo, unmodifiableList(asList("tfl_1-BAK_-2-y05.xml")));
		fileNames.put(Line.Central, unmodifiableList(asList("tfl_1-CEN_-670103-y05.xml")));
		fileNames.put(Line.Circle, unmodifiableList(asList("tfl_1-CIR_-290103-y05.xml")));
		fileNames.put(Line.District, unmodifiableList(asList("tfl_1-DIS_-1430113-y05.xml")));
		fileNames.put(Line.HammersmithAndCity, unmodifiableList(asList("tfl_1-HAM_-290103-y05.xml")));
		fileNames.put(Line.Jubilee, unmodifiableList(asList("tfl_1-JUB_-120140-y05.xml")));
		fileNames.put(Line.Metropolitan, unmodifiableList(asList("tfl_1-MET_-3330101-y05.xml")));
		fileNames.put(Line.Northern, unmodifiableList(asList("tfl_1-NTN_-530103-y05.xml")));
		fileNames.put(Line.Piccadilly, unmodifiableList(asList("tfl_1-PIC_-500101-y05.xml")));
		fileNames.put(Line.Victoria, unmodifiableList(asList("tfl_1-VIC_-350112-y05.xml")));
		fileNames.put(Line.WaterlooAndCity, unmodifiableList(asList("tfl_1-WAC_-60102-y05.xml")));
		fileNames.put(Line.DLR, unmodifiableList(asList("tfl_25-DLR_-6-y05.xml")));
		fileNames.put(Line.EmiratesAirline, unmodifiableList(asList("tfl_71-CABd-1-y05.xml")));
		fileNames.put(Line.Tram, unmodifiableList(asList(
				"tfl_63-TRM1-1-y05.xml",
				"tfl_63-TRM2-1-y05.xml",
				"tfl_63-TRM3-1-y05.xml",
				"tfl_63-TRM4-1-y05.xml"
		)));
		return fileNames;
	}
}
