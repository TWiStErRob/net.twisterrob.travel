package net.twisterrob.blt.io.feeds.facilities;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Facility {
	private static final Logger LOG = LoggerFactory.getLogger(Facility.class);

	private String m_name;
	private String m_value;

	public Facility(String name) {
		m_name = name;
	}

	public Facility(String name, String value) {
		m_name = name;
		m_value = value;
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
		return String.format(Locale.ROOT, "%s=%s", m_name, m_value);
	}

	@SuppressWarnings("PointlessBooleanExpression")
	public boolean hasValue() {
		return m_name != null &&
				"Ticket Halls".equals(m_name) && getIntValue() > 0
				|| "Lifts".equals(m_name) && getIntValue() > 0
				// \d+ => \0, yes (disabled only) => 1
				|| "Escalators".equals(m_name) && getIntValue() > 0
				|| "Gates".equals(m_name) && getIntValue() > 0
				|| "Toilets".equals(m_name) && getBoolValue() == true
				|| "Photo Booths".equals(m_name) && getIntValue() > 0
				|| "Cash Machines".equals(m_name) && getIntValue() > 0
				|| "Car park".equals(m_name) && getBoolValue() == true
				|| "Bridge".equals(m_name) && getBoolValue() == true
				|| "Waiting Room".equals(m_name) && getBoolValue() == true
				// Payphones=14 in ticket halls, 4 on platforms =>
				// Payphones in ticket halls = 14
				// Payphones on platforms = 4
				|| m_name.startsWith("Payphones") && getIntValue() > 0
				;
	}

	public boolean getBoolValue() {
		return Boolean.parseBoolean(m_value) || "yes".equalsIgnoreCase(m_value);
	}

	public int getIntValue() {
		try {
			return Integer.parseInt(m_value);
		} catch (NumberFormatException ex) {
			LOG.warn("Invalid int value: %s=%s", m_name, m_value);
			return -1;
		}
	}

	public String getStringValue() {
		return m_value;
	}
}
