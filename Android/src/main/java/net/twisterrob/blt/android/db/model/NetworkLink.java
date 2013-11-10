package net.twisterrob.blt.android.db.model;

import net.twisterrob.blt.model.Line;

public class NetworkLink implements Comparable<NetworkLink> {
	public final NetworkNode m_target;
	public final int m_distance;
	private Line m_line;

	public NetworkLink(NetworkNode target, Line line, int distance) {
		m_target = target;
		m_line = line;
		m_distance = distance;
	}

	public Line getLine() {
		return m_line;
	}

	public NetworkNode getTarget() {
		return m_target;
	}

	public int getDistance() {
		return m_distance;
	}

	public int compareTo(NetworkLink another) {
		int target = m_target.compareTo(another.m_target);
		return target != 0? target : m_line.compareTo(another.m_line);
	}

	@Override
	public String toString() {
		return String.format("to %s (%s/%d)", m_target, m_line, m_distance);
	}
}
