package net.twisterrob.blt.io.feeds.trackernet;

import static java.util.Collections.*;

import java.util.*;

import net.twisterrob.blt.io.feeds.BaseFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.*;
import net.twisterrob.blt.model.Line;
import net.twisterrob.java.collections.MultiKey;

public class PredictionSummaryFeed extends BaseFeed<PredictionSummaryFeed> {
	private Date m_timeStamp = new Date();
	private Line m_line = Line.unknown;
	private final List<Station> m_stations = new ArrayList<Station>();
	private final List<Station> m_alienStations = new ArrayList<Station>();
	private final List<Platform> m_platforms = new ArrayList<Platform>();
	private final List<Train> m_trains = new ArrayList<Train>();
	private final Map<Station, List<Platform>> m_stationPlatform = new HashMap<Station, List<Platform>>();
	private final Map<MultiKey, List<Train>> m_stationPlatformTrain = new HashMap<MultiKey, List<Train>>();

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

	@Override
	protected void postProcess() {
		super.postProcess();
		if (m_line != null) {
			Map<String, Line> alienLines = StationIncosistencies.EXTAS.get(m_line);
			for (Station station: m_stations) {
				Line line = alienLines.get(station.getName());
				if (line == null) {
					line = m_line;
				}
				station.setLine(line);
			}
		}
	}

	public void applyAliases() {
		Map<String, String> map = StationIncosistencies.TRACKERNET_TO_TIMETABLE_ALIASES.get(m_line);
		for (Station station: m_stations) {
			String newName = map.get(station.getName());
			if (newName != null) {
				station.setName(newName);
			}
		}
	}

	public List<Station> segregateAlienStations() {
		for (Iterator<Station> iterator = m_stations.iterator(); iterator.hasNext();) {
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
			platforms = new ArrayList<Platform>();
			m_stationPlatform.put(station, platforms);
		}
		platforms.add(platform);
		m_platforms.add(platform);

		MultiKey key = new MultiKey(station, platform);
		List<Train> trains = m_stationPlatformTrain.get(key);
		if (trains == null) {
			trains = new ArrayList<Train>();
			m_stationPlatformTrain.put(key, trains);
		}
	}

	public void addTrain(Station station, Platform platform, Train train) {
		MultiKey key = new MultiKey(station, platform);
		List<Train> trains = m_stationPlatformTrain.get(key);
		if (trains == null) {
			trains = new ArrayList<Train>();
			m_stationPlatformTrain.put(key, trains);
		}
		trains.add(train);
		m_trains.add(train);
	}

	public Map<Platform, List<Train>> collectTrains(Station station) {
		Map<Platform, List<Train>> result = new TreeMap<Platform, List<Train>>(new Comparator<Platform>() {
			@Override
			public int compare(Platform platform1, Platform platform2) {
				int p1 = platform1.extractPlatformNumber();
				int p2 = platform2.extractPlatformNumber();
				int dirDiff = platform1.getDirection().compareTo(platform2.getDirection());
				int nameDiff = platform1.getName().compareTo(platform2.getName());
				return (p1 < p2? -1 : (p1 == p2? (p1 != 0? 0 : dirDiff != 0? dirDiff : nameDiff) : 1));
			}
		});
		for (Platform platform: m_stationPlatform.get(station)) {
			List<Train> trains = m_stationPlatformTrain.get(new MultiKey(station, platform));
			result.put(platform, trains);
		}
		return result;
	}

	public List<Train> collectTrains(Station station, Platform platform) {
		return unmodifiableList(m_stationPlatformTrain.get(new MultiKey(station, platform)));
	}
}
