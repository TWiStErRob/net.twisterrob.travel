package com.twister.london.travel.model;
public class LineStatus {
	private String m_id;
	private String m_description;
	private boolean m_active;

	public String getId() {
		return m_id;
	}
	public void setId(String id) {
		m_id = id;
	}

	public String getDescription() {
		return m_description;
	}
	public void setDescription(String description) {
		m_description = description;
	}

	public boolean isActive() {
		return m_active;
	}
	public void setActive(boolean isActive) {
		m_active = isActive;
	}

}
