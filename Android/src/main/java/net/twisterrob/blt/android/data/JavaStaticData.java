package net.twisterrob.blt.android.data;

import java.util.*;

import net.twisterrob.blt.android.R;
import net.twisterrob.blt.model.StopType;

public class JavaStaticData implements StaticData {
	private Map<StopType, Integer> stopTypeLogos = new EnumMap<StopType, Integer>(StopType.class);

	public JavaStaticData() {
		initStopTypeDrawables();
	}

	private void initStopTypeDrawables() {
		stopTypeLogos.put(StopType.unknown, R.drawable.tfl_roundel_tfl);
		stopTypeLogos.put(StopType.Underground, R.drawable.tfl_roundel_lul);
		stopTypeLogos.put(StopType.Overground, R.drawable.tfl_roundel_overground);
		stopTypeLogos.put(StopType.DLR, R.drawable.tfl_roundel_dlr);
		stopTypeLogos.put(StopType.Tram, R.drawable.tfl_roundel_tramlink);
		stopTypeLogos.put(StopType.Bus, R.drawable.tfl_roundel_bus);
		stopTypeLogos.put(StopType.Coach, R.drawable.tfl_roundel_vcs);
		stopTypeLogos.put(StopType.Taxi, R.drawable.tfl_roundel_taxiph);
		stopTypeLogos.put(StopType.Water, R.drawable.tfl_roundel_lrs);
		stopTypeLogos.put(StopType.Cycle, R.drawable.tfl_roundel_cyclehire);
		stopTypeLogos.put(StopType.Air, R.drawable.tfl_roundel_airline);
	}

	public Map<StopType, Integer> getStopTypeLogos() {
		return stopTypeLogos;
	}
}
