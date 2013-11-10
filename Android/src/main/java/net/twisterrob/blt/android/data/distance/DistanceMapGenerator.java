package net.twisterrob.blt.android.data.distance;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.model.Line;
import net.twisterrob.java.model.LocationUtils;

import org.slf4j.*;

@NotThreadSafe
public class DistanceMapGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapGenerator.class);

	private Map<Integer, NetworkNode> nodes;
	private NetworkLink startLink;
	private Map<NetworkLink, Double> finishedNodes;

	private final DistanceMapGeneratorConfig config;

	public DistanceMapGenerator(Map<Integer, NetworkNode> networkNodes, NetworkLink startLink,
			DistanceMapGeneratorConfig config) {
		this.nodes = networkNodes;
		this.startLink = startLink;
		this.config = config;
		double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for (NetworkNode node: nodes.values()) {
			double lat = node.getPos().getLatitude();
			double lon = node.getPos().getLongitude();
			if (lon < minX) {
				minX = lon;
			} else if (maxX < lon) {
				maxX = lon;
			}
			if (lat < minY) {
				minY = lat;
			} else if (maxY < lat) {
				maxY = lat;
			}
		}
		// *2 makes the pixels square, because the geo coordinate system is twice as wide as tall
	}

	public DistanceMapGeneratorConfig getConfig() {
		return config;
	}

	public Map<NetworkLink, Double> generate() {
		finishedNodes = new TreeMap<NetworkLink, Double>();
		finishedNodes.put(startLink, config.minutes);
		traverse(startLink, config.minutes);
		for (Entry<NetworkLink, Double> circle: finishedNodes.entrySet()) {
			double remainingMinutes = circle.getValue();
			double remainingWalk = (remainingMinutes - config.timePlatformToStreet) / 60.0 /* to hours */
					* config.speedOnFoot * 1000.0 /* to meters */;
			circle.setValue(remainingWalk);

		}
		return finishedNodes;
	}

	/**
	 * @param node current tube station
	 * @param remainingMinutes minutes remaining from the possible trips
	 * @return 
	 */
	private boolean traverse(NetworkLink from, double remainingMinutes) {
		if (remainingMinutes < 0) {
			return false;
		} else {
			finishedNodes.put(from, remainingMinutes);
		}
		NetworkNode node = from.m_target;
		for (NetworkLink to: node.out) {
			Double oldRemaining = finishedNodes.get(to);
			double travelWithTube = config.tubingStrategy.distance(from, to);
			double newRemaining = remainingMinutes - travelWithTube;
			if (oldRemaining == null || oldRemaining < newRemaining) {
				traverse(to, newRemaining);
			}
		}
		//if (0 < remainingWalk) {
		//	walkFromStation(node, remainingWalk);
		//}
		return true;
	}

	@SuppressWarnings("unused")
	// TODO too slow
	private void walkFromStation(NetworkNode start, double remainingWalk) {
		for (NetworkNode node: nodes.values()) {
			double dist = LocationUtils.distance(start.getPos(), node.getPos());
			if (10 < dist && dist < remainingWalk) {
				double remainingMeters = remainingWalk - dist;
				double remainingMinutes = remainingMeters / 1000.0 / config.speedOnFoot * 60;
				traverse(new NetworkLink(node, Line.unknown, 0), remainingMinutes - config.timePlatformToStreet);
			}
		}
	}
}
