package net.twisterrob.blt.android.data.distance;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.java.model.LocationUtils;

import org.slf4j.*;

@NotThreadSafe
public class DistanceMapGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapGenerator.class);

	private Set<NetworkNode> nodes;
	private NetworkNode startNode;
	private Map<NetworkNode, Double> finishedNodes;

	private final DistanceMapGeneratorConfig config;

	public DistanceMapGenerator(Set<NetworkNode> networkNodes, NetworkNode startNode, DistanceMapGeneratorConfig config) {
		this.nodes = networkNodes;
		this.startNode = startNode;
		this.config = config;
		double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
		for (NetworkNode node: nodes) {
			double lat = node.getLocation().getLatitude();
			double lon = node.getLocation().getLongitude();
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

	public Map<NetworkNode, Double> generate() {
		finishedNodes = new HashMap<NetworkNode, Double>();
		finishedNodes.put(startNode, config.minutes);
		traverse(startNode, config.minutes);
		for (Entry<NetworkNode, Double> circle: finishedNodes.entrySet()) {
			double remainingMinutes = circle.getValue();
			double remainingWalk = (remainingMinutes - config.timePlatformToStreet) / 60.0 /* to hours */
					* config.speedOnFoot * 1000.0 /* to meters */;
			NetworkNode node = circle.getKey();
			LOG.debug("Converting result for {}/{}: {} min -> {} m", //
					node.getLine(), node.getName(), remainingMinutes, remainingWalk);
			circle.setValue(remainingWalk);

		}
		return finishedNodes;
	}

	/**
	 * @param from current tube station
	 * @param remainingMinutes minutes remaining from the possible trips
	 * @return 
	 */
	private boolean traverse(NetworkNode from, double remainingMinutes) {
		if (remainingMinutes < 0) {
			return false;
		} else {
			finishedNodes.put(from, remainingMinutes);
		}
		for (NetworkLink link: from.getOut()) {
			NetworkNode to = link.getTarget();
			Double oldRemaining = finishedNodes.get(to);
			double travelWithTube = config.tubingStrategy.distance(link);
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
		for (NetworkNode node: nodes) {
			double dist = LocationUtils.distance(start.getLocation(), node.getLocation());
			if (10 < dist && dist < remainingWalk) {
				double remainingMeters = remainingWalk - dist;
				double remainingMinutes = remainingMeters / 1000.0 / config.speedOnFoot * 60;
				traverse(node, remainingMinutes - config.timePlatformToStreet);
			}
		}
	}
}
