package net.twisterrob.blt.android.data.distance;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.android.db.model.*;
import net.twisterrob.java.model.*;

import org.slf4j.*;

@NotThreadSafe
public class DistanceMapGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(DistanceMapGenerator.class);

	private final Set<NetworkNode> nodes;
	private Set<NetworkNode> startNodes;
	private Map<NetworkNode, Double> finishedNodes;

	private final DistanceMapGeneratorConfig config;

	public DistanceMapGenerator(Set<NetworkNode> networkNodes, DistanceMapGeneratorConfig config) {
		this.nodes = networkNodes;
		this.config = config;
	}

	public DistanceMapGeneratorConfig getConfig() {
		return config;
	}

	public Collection<NetworkNode> getStartNodes() {
		return startNodes;
	}

	public Map<NetworkNode, Double> generate(Location location) {
		startNodes = new HashSet<NetworkNode>();
		findStartNodes(location);
		finishedNodes = new HashMap<NetworkNode, Double>();
		for (NetworkNode start: startNodes) {
			finishedNodes.put(start, config.minutes);
			traverse(start, config.minutes);
		}
		for (Entry<NetworkNode, Double> circle: finishedNodes.entrySet()) {
			double remainingMinutes = circle.getValue();
			double remainingWalk = (remainingMinutes - config.timePlatformToStreet) / 60.0 /* to hours */
					* config.speedOnFoot * 1000.0 /* to meters */;
			//LOG.debug("Converting result for {}/{}: {} min -> {} m", //
			//		circle.getKey().getLine(), circle.getKey().getName(), remainingMinutes, remainingWalk);
			circle.setValue(remainingWalk);

		}
		return finishedNodes;
	}

	private void findStartNodes(Location location) {
		double bestDistance = Double.POSITIVE_INFINITY;
		NetworkNode closest = null;
		for (NetworkNode node: nodes) {
			double distance = LocationUtils.distance(location, node.getLocation());
			if (distance < bestDistance) {
				bestDistance = distance;
				closest = node;
			}
		}
		startNodes.add(closest);
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
		if (config.transferInStation) {
			for (NetworkNode to: from.getNeighbors()) {
				Double oldRemaining = finishedNodes.get(to);
				double newRemaining = remainingMinutes - config.timeTransfer;
				if (oldRemaining == null || oldRemaining < newRemaining) {
					traverse(to, newRemaining);
				}
			}
		}
		if (config.transferWalk) {
			for (Entry<NetworkNode, Double> distEntry: from.getDists().entrySet()) {
				NetworkNode to = distEntry.getKey();
				Double oldRemaining = finishedNodes.get(to);
				double walkToOtherStation = distEntry.getValue() / 1000.0 / config.speedOnFoot * 60;
				walkToOtherStation = config.timePlatformToStreet + walkToOtherStation + config.timePlatformToStreet;
				double newRemaining = remainingMinutes - walkToOtherStation;
				if (oldRemaining == null || oldRemaining < newRemaining) {
					traverse(to, newRemaining);
				}
			}
		}
		return true;
	}
}
