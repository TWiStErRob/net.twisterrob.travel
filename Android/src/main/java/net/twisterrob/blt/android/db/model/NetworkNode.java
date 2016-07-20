package net.twisterrob.blt.android.db.model;

import java.util.*;

import net.twisterrob.blt.model.Line;
import net.twisterrob.java.model.Location;

public class NetworkNode {
	private final int m_id;
	private final String m_name;
	private final Line m_line;
	private final Location m_pos;

	public NetworkNode(int id, String name, Line line, Location location) {
		m_id = id;
		m_name = name;
		m_line = line;
		m_pos = location;
	}

	private final Set<NetworkLink> out = new HashSet<>();
	private final Set<NetworkNode> neighbors = new HashSet<>();
	private final Map<NetworkNode, Double> dists = new HashMap<>();

	public int getID() {
		return m_id;
	}

	public String getName() {
		return m_name;
	}

	public Line getLine() {
		return m_line;
	}

	public Location getLocation() {
		return m_pos;
	}

	public Set<NetworkLink> getOut() {
		return out;
	}

	public Set<NetworkNode> getNeighbors() {
		return neighbors;
	}

	public Map<NetworkNode, Double> getDists() {
		return dists;
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "%s (%s/%d)", m_name, m_line, m_id);
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_id;
		result = prime * result + ((m_line == null)? 0 : m_line.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NetworkNode)) {
			return false;
		}
		NetworkNode other = (NetworkNode)obj;
		if (m_id != other.m_id) {
			return false;
		}
		if (m_line != other.m_line) {
			return false;
		}
		return true;
	}

	public static NetworkNode find(Iterable<NetworkNode> nodes, String name, Line line) {
		for (NetworkNode node : nodes) {
			if (node.getName().equals(name) && node.getLine() == line) {
				return node;
			}
		}
		return null;
	}
}
