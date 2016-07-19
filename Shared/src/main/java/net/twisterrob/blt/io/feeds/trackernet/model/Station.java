package net.twisterrob.blt.io.feeds.trackernet.model;

import java.util.Comparator;

import net.twisterrob.blt.model.Line;

public class Station {
	public static final Comparator<Station> COMPARATOR_NAME = new Comparator<Station>() {
		@Override public int compare(Station o1, Station o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	private String m_name;
	private Line m_line;
	private String m_trackerNetCode;

	@Override public String toString() {
		return String.format("%s", m_name);
	}

	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}

	public Line getLine() {
		return m_line;
	}

	public void setLine(Line line) {
		m_line = line;
	}

	public String getTrackerNetCode() {
		return m_trackerNetCode;
	}

	public void setTrackerNetCode(String code) {
		this.m_trackerNetCode = code;
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_trackerNetCode == null)? 0 : m_trackerNetCode.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Station)) {
			return false;
		}
		Station other = (Station)obj;
		if (m_trackerNetCode == null) {
			if (other.m_trackerNetCode != null) {
				return false;
			}
		} else if (!m_trackerNetCode.equals(other.m_trackerNetCode)) {
			return false;
		}
		return true;
	}
}
