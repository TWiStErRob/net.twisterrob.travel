package net.twisterrob.blt.data.algo;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import net.twisterrob.blt.io.feeds.timetable.*;

public class RouteInfo {
	private List<Route> routes;
	private Map<StopPoint, Node> nodes = new TreeMap<>(StopPoint.BY_NAME);
	private Set<Node> junctions = new TreeSet<>();
	private Set<Node> leafs = new TreeSet<>();
	private String printProgress = null;

	public RouteInfo(List<Route> routes) {
		this.routes = routes;
	}
	public List<Route> getRoutes() {
		return routes;
	}
	public Set<Node> getJunctions() {
		return junctions;
	}
	public Set<Node> getLeafs() {
		return leafs;
	}
	public boolean isPrintProgress() {
		return printProgress != null;
	}
	public void setPrintProgress(String leftPaddingChar) {
		this.printProgress = leftPaddingChar;
	}

	public Map<String, Set<StopPoint>> groupByName(boolean collapseLocation) {
		Map<String, Set<StopPoint>> grouping = new HashMap<>();
		for (Route route: routes) {
			for (StopPoint stop: route) {
				String key = stop.getName();
				Set<StopPoint> stops = grouping.get(key);
				if (stops == null) {
					stops = new TreeSet<>(StopPoint.BY_ID);
					grouping.put(key, stops);
				}
				stops.add(stop);
			}
		}
		if (collapseLocation) {
			for (Iterator<Entry<String, Set<StopPoint>>> groupIt = grouping.entrySet().iterator(); groupIt.hasNext();) {
				Entry<String, Set<StopPoint>> group = groupIt.next();
				for (Iterator<StopPoint> stopIt = group.getValue().iterator(); stopIt.hasNext();) {
					StopPoint stop = stopIt.next();
					for (StopPoint stop2: group.getValue()) {
						if (stop != stop2 && stop2.getLocation().equals(stop.getLocation())) {
							stopIt.remove();
						}
					}
				}
				if (group.getValue().size() == 1) {
					//iterator.remove();
				}
			}
		}
		return grouping;
	}

	public void build() {
		for (Route route: routes) {
			for (StopPoint stop: route) {
				nodes.put(stop, new Node(stop));
			}
		}
		for (Route route: routes) {
			for (RouteSection section: route.getRouteSections()) {
				for (RouteLink link: section.getRouteLinks()) {
					StopPoint from = link.getFrom();
					StopPoint to = link.getTo();
					Node fromNode = nodes.get(from);
					Node toNode = nodes.get(to);
					fromNode.out.put(toNode, State.EMPTY);
					toNode.in.put(fromNode, State.EMPTY);
				}
			}
		}
	}

	public void analyze() {
		Node first = nodes.values().iterator().next();
		if (printProgress != null) {
			System.out.printf("Starting from: ", first);
		}
		dfs(first, 0);
	}
	protected void printGraph() {
		for (Node node: nodes.values()) {
			System.out.printf("%s(%s)\n", node, node.state);
			for (Entry<Node, State> edge: node.out.entrySet()) {
				System.out.printf("\t%s --%s-> %s\n", node, edge.getValue(), edge.getKey());
			}
		}
	}
	private void dfs(Node node, int level) {
		nodeStateChange(level, State.VISIT, node);
		switch (node.out.size()) {
			case 0: // trains only go there (weird)
			case 1: // it has two neighbors, but it's the same
				leafs.add(node);
				break;
			case 2:
				// general station with two neighbors
				break;
			default:
				junctions.add(node);
				break;
		}
		for (Entry<Node, State> edge: node.out.entrySet()) {
			if (edge.getValue() == State.EMPTY) {
				Node child = edge.getKey();
				if (child.state == State.EMPTY) {
					edgeStateChange(level, State.VISIT, node, edge);
					dfs(child, level + 1);
				} else {
					edgeStateChange(level, State.CLOSE, node, edge);
				}
			}
		}
		nodeStateChange(level, State.CLOSE, node);
	}
	private void nodeStateChange(int level, State newState, Node node) {
		if (printProgress != null) {
			String padding = new String(new char[level]).replace("\0", printProgress);
			State oldState = node.state;
			String nodeName = node.data.getName();
			System.out.printf("%snode (%s->%s): %s\n", padding, oldState, newState, nodeName);
		}
		node.state = newState;
	}
	private void edgeStateChange(int level, State newState, Node fromNode, Entry<Node, State> edge) {
		if (printProgress != null) {
			String padding = new String(new char[level]).replace("\0", printProgress);
			State oldState = edge.getValue();
			Node toNode = edge.getKey();
			System.out.printf("%sedge (%s->%s): %s to %s\n", //
					padding, oldState, newState, fromNode, toNode);
		}
		edge.setValue(newState);
	}
	static enum State {
		EMPTY,
		VISIT,
		CLOSE;
	}

	static class Node implements Comparable<Node> {
		@Nonnull StopPoint data;
		final @Nonnull Map<Node, State> in = new TreeMap<>();
		final @Nonnull Map<Node, State> out = new TreeMap<>();
		State state = State.EMPTY;
		public Node(@Nonnull StopPoint data) {
			this.data = data;
		}

		@Override
		public String toString() {
			return String.format("%s[%s]", data.getName(), state);
		}
		public int compareTo(Node o) {
			return StopPoint.BY_NAME.compare(this.data, o.data);
		}
	}
}
