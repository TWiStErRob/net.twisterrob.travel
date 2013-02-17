package com.twister.london.travel.model;
public class Zone {
	private int m_zone;

	public Zone(int zone) {
		m_zone = zone;
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_zone;
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Zone)) {
			return false;
		}
		Zone other = (Zone)obj;
		if (m_zone != other.m_zone) {
			return false;
		}
		return true;
	}

	@Override public String toString() {
		return String.format("Zone %d", m_zone);
	}

}
