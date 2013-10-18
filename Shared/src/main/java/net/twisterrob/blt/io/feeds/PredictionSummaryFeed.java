package net.twisterrob.blt.io.feeds;

import java.util.*;

import net.twisterrob.blt.model.*;
import net.twisterrob.java.collections.MultiKey;

public class PredictionSummaryFeed extends BaseFeed {
	private Date m_timeStamp;
	private Line m_line;
	private List<Station> m_stations;
	private List<Platform> m_platforms;
	private List<Train> m_trains;
	private Map<Station, List<Platform>> m_stationPlatform;
	private Map<MultiKey, List<Train>> m_stationPlatformTrain;

	public PredictionSummaryFeed() {
		m_stations = new ArrayList<Station>();
		m_platforms = new ArrayList<Platform>();
		m_trains = new ArrayList<Train>();
		m_stationPlatform = new HashMap<Station, List<Platform>>();
		m_stationPlatformTrain = new HashMap<MultiKey, List<Train>>();
	}

	public Line getLine() {
		return m_line;
	}
	public void setLine(Line line) {
		this.m_line = line;
	}

	public Date getTimeStamp() {
		return m_timeStamp;
	}
	public void setTimeStamp(Date date) {
		this.m_timeStamp = date;
	}

	public List<Station> getStations() {
		return Collections.unmodifiableList(m_stations);
	}

	public Map<Station, List<Platform>> getStationPlatform() {
		return m_stationPlatform;
	}

	@Override
	protected void postProcess() {
		super.postProcess();
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
		return Collections.unmodifiableList(m_stationPlatformTrain.get(new MultiKey(station, platform)));
	}
}
