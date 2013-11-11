package net.twisterrob.blt.android.data.distance;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.concurrent.NotThreadSafe;

import net.twisterrob.blt.android.db.model.*;

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
			//LOG.debug("Converting result for {}/{}: {} min -> {} m", //
			//		circle.getKey().getLine(), circle.getKey().getName(), remainingMinutes, remainingWalk);
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
		for (NetworkNode to: from.getNeighbors()) {
			Double oldRemaining = finishedNodes.get(to);
			double newRemaining = remainingMinutes - config.timeTransfer;
			if (oldRemaining == null || oldRemaining < newRemaining) {
				traverse(to, newRemaining);
			}
		}
		for (Entry<NetworkNode, Double> distEntry: from.getDists().entrySet()) {
			NetworkNode to = distEntry.getKey();
			Double oldRemaining = finishedNodes.get(to);
			double distMinutes = config.timePlatformToStreet + distEntry.getValue() / 1000.0 / config.speedOnFoot * 60
					+ config.timePlatformToStreet;
			double newRemaining = remainingMinutes - distMinutes;
			if (oldRemaining == null || oldRemaining < newRemaining) {
				traverse(to, newRemaining);
			}
		}
		return true;
	}
}
