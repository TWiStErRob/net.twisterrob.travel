package net.twisterrob.blt.android.data;

import java.util.EnumMap;
import java.util.Map;

import net.twisterrob.blt.android.component.data.R;
import net.twisterrob.blt.data.SharedStaticData;
import net.twisterrob.blt.model.StopType;

public class AndroidHardcodedStaticData extends SharedStaticData implements AndroidStaticData {
	private Map<StopType, Integer> stopTypeLogos = new EnumMap<>(StopType.class);
	private Map<StopType, Integer> stopTypeMapIcons = new EnumMap<>(StopType.class);
	private Map<StopType, Integer> stopTypeMiniIcons = new EnumMap<>(StopType.class);

	public AndroidHardcodedStaticData() {
		initDefaults();
		initStopTypeDrawables();
		initStopTypeMapIcons();
		initStopTypeMiniIcons();
	}

	/** Make sure all stop types have some drawable associated */
	private void initDefaults() {
		for (StopType type : StopType.values()) {
			stopTypeLogos.put(type, R.drawable.tfl_roundel_tfl);
			stopTypeMapIcons.put(type, R.drawable.tfl_roundel_lul_map);
			stopTypeMiniIcons.put(type, R.drawable.tfl_roundel_lul_mini);
		}
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

	private void initStopTypeMapIcons() {
		stopTypeMapIcons.put(StopType.Underground, R.drawable.tfl_roundel_lul_map);
		stopTypeMapIcons.put(StopType.DLR, R.drawable.tfl_roundel_dlr_map);
		stopTypeMapIcons.put(StopType.Overground, R.drawable.tfl_roundel_overground_map);
		stopTypeMapIcons.put(StopType.Tram, R.drawable.tfl_roundel_tramlink_map);
	}

	private void initStopTypeMiniIcons() {
		stopTypeMiniIcons.put(StopType.Underground, R.drawable.tfl_roundel_lul_mini);
		stopTypeMiniIcons.put(StopType.DLR, R.drawable.tfl_roundel_dlr_mini);
		stopTypeMiniIcons.put(StopType.Overground, R.drawable.tfl_roundel_overground_mini);
	}

	@Override public Map<StopType, Integer> getStopTypeLogos() {
		return stopTypeLogos;
	}

	@Override public Map<StopType, Integer> getStopTypeMapIcons() {
		return stopTypeMapIcons;
	}

	@Override public Map<StopType, Integer> getStopTypeMiniIcons() {
		return stopTypeMiniIcons;
	}
}
