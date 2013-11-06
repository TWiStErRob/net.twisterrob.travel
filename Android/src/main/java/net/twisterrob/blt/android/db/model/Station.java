package net.twisterrob.blt.android.db.model;

import java.util.*;

import net.twisterrob.blt.model.*;
import net.twisterrob.java.model.Location;

public class Station {
	public static final Comparator<Station> COMPARATOR_NAME = new Comparator<Station>() {
		@Override
		public int compare(Station o1, Station o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};
	private int m_id;
	private String m_name;
	private String m_address;
	private String m_telephone;
	private Location m_location;
	private StopType m_type = StopType.unknown;
	private List<Line> m_lines;
	private Map<Line, String> m_trackerNetCodes;

	@Override
	public String toString() {
		return String.format("%s", m_name);
	}

	public int getId() {
		return m_id;
	}

	public void setId(final int id) {
		m_id = id;
	}

	public String getName() {
		return m_name;
	}

	public void setName(final String name) {
		m_name = name;
	}

	public String getAddress() {
		return m_address;
	}

	public void setAddress(final String address) {
		m_address = address;
	}

	public String getTelephone() {
		return m_telephone;
	}

	public void setTelephone(final String telephone) {
		m_telephone = telephone;
	}

	public Location getLocation() {
		return m_location;
	}

	public void setLocation(final Location location) {
		m_location = location;
	}

	public StopType getType() {
		return m_type;
	}

	public void setType(StopType type) {
		if (type == null) {
			throw new IllegalArgumentException("Type must be set, use Type." + StopType.unknown + " if not sure");
		}
		m_type = type;
	}

	public List<Line> getLines() {
		return m_lines;
	}

	public void setLines(List<Line> lines) {
		m_lines = lines;
	}

	public String getTrackerNetCode(Line line) {
		return m_trackerNetCodes != null? m_trackerNetCodes.get(line) : null;
	}

	public void setTrackerNetCodes(Map<Line, String> codes) {
		m_trackerNetCodes = codes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + m_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
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
		if (m_id != other.m_id) {
			return false;
		}
		return true;
	}
}
