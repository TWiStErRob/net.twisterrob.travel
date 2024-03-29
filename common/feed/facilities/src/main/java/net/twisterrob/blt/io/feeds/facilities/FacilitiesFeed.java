package net.twisterrob.blt.io.feeds.facilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.twisterrob.blt.io.feeds.BaseFeed;
import net.twisterrob.blt.model.Line;

public class FacilitiesFeed extends BaseFeed<FacilitiesFeed> {
	private List<Station> m_stations;
	private Map<Line, List<Station>> m_lines;
	private Map<String, List<Station>> m_facilities;
	private Map<Zone, List<Station>> m_zones;
	private Map<String, String> m_styles;

	public FacilitiesFeed() {
		m_styles = new HashMap<>();
	}

	public List<Station> getStations() {
		return m_stations;
	}
	public void setStations(List<Station> stations) {
		m_stations = stations;
	}

	public Map<String, String> getStyles() {
		return m_styles;
	}

	@Override protected void postProcess() {
		super.postProcess();
		if (m_stations != null) {
			m_lines = new HashMap<>();
			m_zones = new HashMap<>();
			m_facilities = new HashMap<>();
			for (Station station : m_stations) {
				for (Line line : station.getLines()) {
					if (!m_lines.containsKey(line)) {
						m_lines.put(line, new ArrayList<Station>());
					}
					List<Station> stations = m_lines.get(line);
					stations.add(station);
				}
				for (Zone zone : station.getZones()) {
					if (!m_zones.containsKey(zone)) {
						m_zones.put(zone, new ArrayList<Station>());
					}
					List<Station> stations = m_zones.get(zone);
					stations.add(station);
				}
				for (Facility facility : station.getFacilities()) {
					if (facility.hasValue()) {
						String facilityName = facility.getName();
						if (!m_facilities.containsKey(facilityName)) {
							m_facilities.put(facilityName, new ArrayList<Station>());
						}
						List<Station> stations = m_facilities.get(facilityName);
						stations.add(station);
					}
				}
			}
		}
	}
}
