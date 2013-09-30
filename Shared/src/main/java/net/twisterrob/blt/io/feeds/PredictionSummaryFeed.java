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
	void postProcess() {
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
		Map<Platform, List<Train>> result = new HashMap<Platform, List<Train>>();
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
