package net.twisterrob.blt.io.feeds.trackernet.model;

import net.twisterrob.blt.model.Line;

public class LineStatus {
	private Line m_line;
	private DelayType m_type;
	private String m_description;
	private boolean m_active;

	public Line getLine() {
		return m_line;
	}
	public void setLine(Line line) {
		m_line = line;
	}

	public DelayType getType() {
		return m_type;
	}
	public void setType(DelayType type) {
		m_type = type;
	}

	public String getDescription() {
		return m_description;
	}
	public void setDescription(String description) {
		if (description != null && description.trim().length() == 0) {
			m_description = null;
		} else {
			m_description = description;
		}
	}

	public boolean isActive() {
		return m_active;
	}
	public void setActive(boolean isActive) {
		m_active = isActive;
	}
}
