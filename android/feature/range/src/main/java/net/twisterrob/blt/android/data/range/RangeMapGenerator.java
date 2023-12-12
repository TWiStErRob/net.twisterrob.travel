package net.twisterrob.blt.android.data.range;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.*;

import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.blt.model.Line;
import net.twisterrob.java.model.*;

@NotThreadSafe
public class RangeMapGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(RangeMapGenerator.class);

	/**
	 * Full system network graph to traverse.
	 */
	private final Set<NetworkNode> nodes;
	/**
	 * Nodes where the search starts. The values are their distances in minutes with the given walking speed.
	 */
	private Map<NetworkNode, Double> startNodes;
	/**
	 * All the nodes that have been seen during traversal along with their best time to reach them.
	 */
	private Map<NetworkNode, Double> distances;

	private final RangeMapGeneratorConfig config;

	public RangeMapGenerator(Set<NetworkNode> networkNodes, RangeMapGeneratorConfig config) {
		this.nodes = networkNodes;
		this.config = config; // TODO make a copy to prevent weirdness if modified async
	}

	public RangeMapGeneratorConfig getConfig() {
		return config;
	}

	public Collection<NetworkNode> getStartNodes() {
		return Collections.unmodifiableSet(startNodes.keySet());
	}

	public Map<NetworkNode, Double> generate(Location location) {
		startNodes = findStartNodes(location);
		distances = new HashMap<>();
		distances.put(new NetworkNode(0, "Walk", Line.unknown, location), (double)config.totalAllottedTime);
		for (Entry<NetworkNode, Double> start : startNodes.entrySet()) {
			Double oldRemaining = distances.get(start.getKey());
			double newRemaining = config.totalAllottedTime
					- start.getValue() // walk to the station
					- config.platformToStreetTime // enter the station
					;
			if (oldRemaining == null || oldRemaining < newRemaining) {
				traverseDepthFirst(start.getKey(), newRemaining);
			}
		}
		// convert all resulting remaining minutes to meters
		for (Entry<NetworkNode, Double> circle : distances.entrySet()) {
			double remainingWalkMinutes = circle.getValue();
			double remainingWalkDistance = (remainingWalkMinutes - config.platformToStreetTime) // exit the station
					/ 60.0 // to hours
					* config.walkingSpeed // to kilometers
					* 1000.0 // to meters
					;
			//LOG.debug("Converting result for {}/{}: {} min -> {} m",
			//		circle.getKey().getLine(), circle.getKey().getName(), remainingWalkMinutes, remainingWalkDistance);
			circle.setValue(remainingWalkDistance);
		}
		return distances;
	}

	private Map<NetworkNode, Double> findStartNodes(Location location) {
		Map<NetworkNode, Double> startNodes = new HashMap<>();
		for (NetworkNode node : nodes) {
			double distanceToStartNode = LocationUtils.distance(location, node.getLocation());
			double minutesToReachStartNode = config.walk(distanceToStartNode);
			if (minutesToReachStartNode < config.initialAllottedWalkTime) {
				startNodes.put(node, minutesToReachStartNode);
			}
		}
		if (startNodes.isEmpty()) {
			// nothing is reachable within the initial walk time, get the closest one anyway so we can still journey
			Entry<NetworkNode, Double> closestNode = findClosestNode(location);
			if (closestNode.getValue() <= config.totalAllottedTime) {
				startNodes.put(closestNode.getKey(), closestNode.getValue());
			}
		}
		return startNodes;
	}

	private Map.Entry<NetworkNode, Double> findClosestNode(Location location) {
		double bestDistance = Double.POSITIVE_INFINITY;
		NetworkNode closest = null;
		for (NetworkNode node : nodes) {
			double distance = LocationUtils.distance(location, node.getLocation());
			if (distance < bestDistance) {
				bestDistance = distance;
				closest = node;
			}
		}
		return new SimpleEntry<>(closest, config.walk(bestDistance));
	}

	/**
	 * @param from current tube station
	 * @param remainingMinutes minutes remaining from the possible trips
	 * @return whether the node was traversed CONSIDER why is this needed, can we shortcut using it?   
	 */
	private boolean traverseDepthFirst(NetworkNode from, double remainingMinutes) {
		if (remainingMinutes <= 0) {
			return false; // we don't have any time left, just give up here
		}
		distances.put(from, remainingMinutes);
		for (NetworkLink link : from.getOut()) {
			NetworkNode to = link.getTarget();
			Double oldRemaining = distances.get(to);
			double travelWithTube = config.tubingStrategy.distance(link);
			double newRemaining = remainingMinutes - travelWithTube;
			if (oldRemaining == null || oldRemaining < newRemaining) {
				traverseDepthFirst(to, newRemaining);
			}
		}
		if (config.allowIntraStationInterchange) {
			for (NetworkNode to : from.getNeighbors()) {
				Double oldRemaining = distances.get(to);
				// TODO use tubingStrategy to calculate this to account for Bank and other weirdos
				double newRemaining = remainingMinutes - config.intraStationInterchangeTime;
				if (oldRemaining == null || oldRemaining < newRemaining) {
					traverseDepthFirst(to, newRemaining);
				}
			}
		}
		if (config.allowInterStationInterchange) {
			for (Entry<NetworkNode, Double> otherStation : from.getDists().entrySet()) {
				NetworkNode to = otherStation.getKey();
				Double bestRemainingTime = distances.get(to);
				final double exitCurrentStation = config.platformToStreetTime;
				final double enterOtherStation = config.platformToStreetTime;
				double timeToOtherStation =
						exitCurrentStation + config.walk(otherStation.getValue()) + enterOtherStation;
				double remainingAtOtherStation = remainingMinutes - timeToOtherStation;
				if (bestRemainingTime == null || bestRemainingTime < remainingAtOtherStation) {
					traverseDepthFirst(to, remainingAtOtherStation);
				}
			}
		}
		return true;
	}
}
