package net.twisterrob.blt.android.db.model;

import java.util.Locale;

public class NetworkLink {
	private final NetworkNode m_source;
	private final NetworkNode m_target;
	private final int m_distance;

	public NetworkLink(NetworkNode source, NetworkNode destination, int distance) {
		m_source = source;
		m_target = destination;
		m_distance = distance;
	}

	public NetworkNode getSource() {
		return m_source;
	}

	public NetworkNode getTarget() {
		return m_target;
	}

	public int getDistance() {
		return m_distance;
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "%s to %s (%s: %dm)",
				m_source.getName(), m_target.getName(), m_source.getLine(), m_distance);
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_source == null)? 0 : m_source.hashCode());
		result = prime * result + ((m_target == null)? 0 : m_target.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NetworkLink)) {
			return false;
		}
		NetworkLink other = (NetworkLink)obj;
		if (m_source == null) {
			if (other.m_source != null) {
				return false;
			}
		} else if (!m_source.equals(other.m_source)) {
			return false;
		}
		if (m_target == null) {
			if (other.m_target != null) {
				return false;
			}
		} else if (!m_target.equals(other.m_target)) {
			return false;
		}
		return true;
	}
}
