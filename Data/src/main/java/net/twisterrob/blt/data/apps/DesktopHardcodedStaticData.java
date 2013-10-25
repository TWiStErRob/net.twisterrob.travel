package net.twisterrob.blt.data.apps;

import java.util.*;

import net.twisterrob.blt.data.SharedStaticData;
import net.twisterrob.blt.model.Line;

public class DesktopHardcodedStaticData extends SharedStaticData implements DesktopStaticData {
	private static final String DATA_ROOT = "../temp/feed15/lultramdlrcablecarriver";

	private final Map<Line, List<String>> m_timetableFilenames = new HashMap<>();

	public DesktopHardcodedStaticData() {
		initTimetableFilenames();
	}

	private void initTimetableFilenames() {
		m_timetableFilenames.put(Line.Bakerloo, Arrays.asList("tfl_1-BAK_-2-y05.xml"));
		m_timetableFilenames.put(Line.Central, Arrays.asList("tfl_1-CEN_-670103-y05.xml"));
		m_timetableFilenames.put(Line.Circle, Arrays.asList("tfl_1-CIR_-290103-y05.xml"));
		m_timetableFilenames.put(Line.District, Arrays.asList("tfl_1-DIS_-1430113-y05.xml"));
		m_timetableFilenames.put(Line.HammersmithAndCity, Arrays.asList("tfl_1-HAM_-290103-y05.xml"));
		m_timetableFilenames.put(Line.Jubilee, Arrays.asList("tfl_1-JUB_-120140-y05.xml"));
		m_timetableFilenames.put(Line.Metropolitan, Arrays.asList("tfl_1-MET_-3330101-y05.xml"));
		m_timetableFilenames.put(Line.Northern, Arrays.asList("tfl_1-NTN_-530103-y05.xml"));
		m_timetableFilenames.put(Line.Piccadilly, Arrays.asList("tfl_1-PIC_-500101-y05.xml"));
		m_timetableFilenames.put(Line.Victoria, Arrays.asList("tfl_1-VIC_-350112-y05.xml"));
		m_timetableFilenames.put(Line.WaterlooAndCity, Arrays.asList("tfl_1-WAC_-60102-y05.xml"));
		m_timetableFilenames.put(Line.DLR, Arrays.asList("tfl_25-DLR_-6-y05.xml"));
		m_timetableFilenames.put(Line.EmiratesAirline, Arrays.asList("tfl_71-CABd-1-y05.xml"));
		m_timetableFilenames.put(Line.Tram, Arrays.asList("tfl_63-TRM1-1-y05.xml", "tfl_63-TRM2-1-y05.xml",
				"tfl_63-TRM3-1-y05.xml", "tfl_63-TRM4-1-y05.xml"));
		//m_files.put(Line.Overground, "");
	}

	public Map<Line, List<String>> getTimetableFilenames() {
		return m_timetableFilenames;
	}

	public String getTimetableRoot() {
		return DATA_ROOT;
	}
}
