package com.twister.london.travel.model;

import java.util.List;

import com.twister.android.utils.model.Location;

public class Station {
	private int m_id;
	private String m_name;
	private String m_address;
	private String m_telephone;
	private Location m_location;
	private List<Zone> m_zones;
	private List<Facility> m_facilities;
	private List<Line> m_lines;
	private Type m_type = Type.Unknown;

	@Override public String toString() {
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

	public List<Zone> getZones() {
		return m_zones;
	}

	public void setZones(List<Zone> zones) {
		m_zones = zones;
	}

	public List<Facility> getFacilities() {
		return m_facilities;
	}

	public void setFacilities(List<Facility> facilities) {
		m_facilities = facilities;
	}

	public List<Line> getLines() {
		return m_lines;
	}

	public void setLines(List<Line> lines) {
		m_lines = lines;
	}

	public Type getType() {
		return m_type;
	}

	public void setType(Type type) {
		if (type == null) {
			throw new IllegalArgumentException("Type must be set, use Type." + Type.Unknown + " if not sure");
		}
		m_type = type;
	}
}
