package com.twister.london.travel.model;
public class LineStatus {
	private String m_description;
	private boolean m_active;
	private LineStatusType m_type;

	public String getDescription() {
		return m_description;
	}
	public void setDescription(String description) {
		if (description != null && description.trim().length() == 0) {
			description = null;
		}
		m_description = description;
	}

	public boolean isActive() {
		return m_active;
	}
	public void setActive(boolean isActive) {
		m_active = isActive;
	}

	public LineStatusType getType() {
		return m_type;
	}
	public void setType(LineStatusType type) {
		m_type = type;
	}

}
