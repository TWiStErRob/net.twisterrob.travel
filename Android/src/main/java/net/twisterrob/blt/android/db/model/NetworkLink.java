package net.twisterrob.blt.android.db.model;

import java.util.Comparator;

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

	public static final Comparator<NetworkLink> LINES_FIRST = new Comparator<NetworkLink>() {
		public int compare(NetworkLink lhs, NetworkLink rhs) {
			int line = lhs.m_line.compareTo(rhs.m_line);
			return line != 0? line : lhs.m_target.compareTo(rhs.m_target);
		}
	};
}
