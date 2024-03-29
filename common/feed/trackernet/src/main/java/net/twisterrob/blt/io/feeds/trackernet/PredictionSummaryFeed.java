package net.twisterrob.blt.io.feeds.trackernet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.unmodifiableList;

import net.twisterrob.blt.io.feeds.BaseFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.Platform;
import net.twisterrob.blt.io.feeds.trackernet.model.Station;
import net.twisterrob.blt.io.feeds.trackernet.model.Train;
import net.twisterrob.blt.model.Line;
import net.twisterrob.java.collections.MultiKey;

public class PredictionSummaryFeed extends BaseFeed<PredictionSummaryFeed> {
	private Date m_timeStamp = new Date();
	private Line m_line = Line.unknown;
	private final List<Station> m_stations = new ArrayList<>();
	private final List<Station> m_alienStations = new ArrayList<>();
	private final List<Platform> m_platforms = new ArrayList<>();
	private final List<Train> m_trains = new ArrayList<>();
	private final Map<Station, List<Platform>> m_stationPlatform = new HashMap<>();
	private final Map<MultiKey, List<Train>> m_stationPlatformTrain = new HashMap<>();

	public Line getLine() {
		return m_line;
	}
	public void setLine(Line line) {
		this.m_line = line;
		postProcess();
	}

	public Date getTimeStamp() {
		return m_timeStamp;
	}
	public void setTimeStamp(Date date) {
		this.m_timeStamp = date;
	}

	public List<Station> getStations() {
		return unmodifiableList(m_stations);
	}

	public List<Station> getAlienStations() {
		return unmodifiableList(m_alienStations);
	}

	public Map<Station, List<Platform>> getStationPlatform() {
		return m_stationPlatform;
	}

	@Override protected void postProcess() {
		super.postProcess();
		if (m_line != null) {
			Map<String, Line> alienLines = StationInconsistencies.EXTRAS.get(m_line);
			for (Station station : m_stations) {
				Line line = alienLines.get(station.getName());
				if (line == null) {
					line = m_line;
				}
				station.setLine(line);
			}
		}
	}

	public void applyAliases() {
		Map<String, String> map = StationInconsistencies.TRACKERNET_TO_TIMETABLE_ALIASES.get(m_line);
		for (Station station : m_stations) {
			String newName = map.get(station.getName());
			if (newName != null) {
				station.setName(newName);
			}
		}
	}

	public List<Station> segregateAlienStations() {
		for (Iterator<Station> iterator = m_stations.iterator(); iterator.hasNext(); ) {
			Station station = iterator.next();
			if (station.getLine() != m_line) {
				iterator.remove();
				m_alienStations.add(station);
			}
		}
		return getAlienStations();
	}

	void addStation(Station station) {
		m_stations.add(station);
	}

	public void addPlatform(Station station, Platform platform) {
		List<Platform> platforms = m_stationPlatform.get(station);
		if (platforms == null) {
			platforms = new ArrayList<>();
			m_stationPlatform.put(station, platforms);
		}
		platforms.add(platform);
		m_platforms.add(platform);

		MultiKey key = new MultiKey(station, platform);
		List<Train> trains = m_stationPlatformTrain.get(key);
		if (trains == null) {
			trains = new ArrayList<>();
			m_stationPlatformTrain.put(key, trains);
		}
	}

	public void addTrain(Station station, Platform platform, Train train) {
		MultiKey key = new MultiKey(station, platform);
		List<Train> trains = m_stationPlatformTrain.get(key);
		if (trains == null) {
			trains = new ArrayList<>();
			m_stationPlatformTrain.put(key, trains);
		}
		trains.add(train);
		m_trains.add(train);
	}

	public Map<Platform, List<Train>> collectTrains(Station station) {
		Map<Platform, List<Train>> result = new TreeMap<>(new Comparator<Platform>() {
			@Override public int compare(Platform platform1, Platform platform2) {
				int p1 = platform1.extractPlatformNumber();
				int p2 = platform2.extractPlatformNumber();
				int dirDiff = platform1.getDirection().compareTo(platform2.getDirection());
				int nameDiff = platform1.getName().compareTo(platform2.getName());
				return (p1 < p2? -1 : (p1 == p2? (p1 != 0? 0 : dirDiff != 0? dirDiff : nameDiff) : 1));
			}
		});
		for (Platform platform : m_stationPlatform.get(station)) {
			List<Train> trains = m_stationPlatformTrain.get(new MultiKey(station, platform));
			result.put(platform, trains);
		}
		return result;
	}

	public List<Train> collectTrains(Station station, Platform platform) {
		return unmodifiableList(m_stationPlatformTrain.get(new MultiKey(station, platform)));
	}
}
