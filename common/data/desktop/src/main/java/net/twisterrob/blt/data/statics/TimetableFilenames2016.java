package net.twisterrob.blt.data.statics;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import net.twisterrob.blt.model.Line;

public class TimetableFilenames2016 implements StaticDataFragment<Map<Line, List<String>>> {
	@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
	public Map<Line, List<String>> init() {
		Map<Line, List<String>> fileNames = new TreeMap<>();
		fileNames.put(Line.Bakerloo, unmodifiableList(asList("tfl_1-BAK-_-y05-400333.xml")));
		fileNames.put(Line.Central, unmodifiableList(asList("tfl_1-CEN-_-y05-690800.xml")));
		fileNames.put(Line.Circle, unmodifiableList(asList("tfl_1-CIR-_-y05-515601.xml")));
		fileNames.put(Line.District, unmodifiableList(asList("tfl_1-DIS-_-y05-1480101.xml")));
		fileNames.put(Line.HammersmithAndCity, unmodifiableList(asList("tfl_1-HAM-_-y05-515601.xml")));
		fileNames.put(Line.Jubilee, unmodifiableList(asList("tfl_1-JUB-_-y05-525601.xml")));
		fileNames.put(Line.Metropolitan, unmodifiableList(asList("tfl_1-MET-_-y05-12.xml")));
		fileNames.put(Line.Northern, unmodifiableList(asList("tfl_1-NTN-_-y05-550101.xml")));
		fileNames.put(Line.Piccadilly, unmodifiableList(asList("tfl_1-PIC-_-y05-560111.xml")));
		fileNames.put(Line.Victoria, unmodifiableList(asList("tfl_1-VIC-_-y05-400800.xml")));
		fileNames.put(Line.WaterlooAndCity, unmodifiableList(asList("tfl_1-WAC-_-y05-60104.xml")));
		fileNames.put(Line.DLR, unmodifiableList(asList("tfl_25-DLR-_-y05-119.xml")));
		fileNames.put(Line.EmiratesAirline, unmodifiableList(asList("tfl_71-CAB-_-y05-1.xml")));
		fileNames.put(Line.Tram, unmodifiableList(asList("tfl_63-TR-_-y05-13.xml")));
		return fileNames;
	}
}
