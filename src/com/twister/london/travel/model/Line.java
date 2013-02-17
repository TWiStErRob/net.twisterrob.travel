package com.twister.london.travel.model;
public class Line {

	private String m_name;
	public Line(String name) {
		m_name = name;
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_name == null)? 0 : m_name.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Line)) {
			return false;
		}
		Line other = (Line)obj;
		if (m_name == null) {
			if (other.m_name != null) {
				return false;
			}
		} else if (!m_name.equals(other.m_name)) {
			return false;
		}
		return true;
	}

	@Override public String toString() {
		return String.format("%s", m_name);
	}

}
