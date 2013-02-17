package com.twister.london.travel.model;
public class Facility {

	private String m_name;
	private String m_value;

	public Facility(String name) {
		m_name = name;
	}

	public String getName() {
		return m_name;
	}
	public void setName(String name) {
		m_name = name;
	}
	public String getValue() {
		return m_value;
	}
	public void setValue(String value) {
		m_value = value;
	}

	@Override public String toString() {
		return String.format("%s=%s", m_name, m_value);
	}

	public boolean hasValue() {
		return "Ticket Halls".equals(m_name) && getIntValue() > 0 //
				|| "Lifts".equals(m_name) && getIntValue() > 0 //
				|| "Escalators".equals(m_name) && getIntValue() > 0 //
				|| "Gates".equals(m_name) && getIntValue() > 0 //
				|| "Toilets".equals(m_name) && getBoolValue() == true //
				|| "Photo Booths".equals(m_name) && getIntValue() > 0 //
				|| "Cash Machines".equals(m_name) && getIntValue() > 0 //
				|| "Payphones".equals(m_name) && getIntValue() > 0 //
				|| "Car park".equals(m_name) && getBoolValue() == true //
				|| "Bridge".equals(m_name) && getBoolValue() == true //
				|| "Waiting Room".equals(m_name) && getBoolValue() == true //
		;
	}

	public boolean getBoolValue() {
		return Boolean.parseBoolean(m_value) || "yes".equalsIgnoreCase(m_value);
	}

	public int getIntValue() {
		return Integer.parseInt(m_value);
	}

	public String getStringValue() {
		return m_value;
	}
}
