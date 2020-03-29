package net.twisterrob.blt.io.feeds.trackernet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.twisterrob.blt.model.Line;

public class LineStatus {
	private Line m_line;
	private DelayType m_type;
	private String m_description;
	private boolean m_active;
	private List<BranchStatus> m_branches = new ArrayList<>();

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

	public void addBranchStatus(BranchStatus lineStatus) {
		m_branches.add(lineStatus);
	}

	public List<BranchStatus> getBranchStatuses() {
		return Collections.unmodifiableList(m_branches);
	}

	public String getBranchDescription() {
		StringBuilder sb = new StringBuilder();
		if (!getBranchStatuses().isEmpty()) {
			sb.append("Affected branches:\n");
		}
		for (BranchStatus branch : getBranchStatuses()) {
			sb.append(branch.getFromStation());
			sb.append(" - ");
			sb.append(branch.getToStation());
			sb.append(";\n");
		}
		return sb.toString();
	}

	public static class BranchStatus {
		private String fromStation;
		private String toStation;

		public BranchStatus() {}

		public BranchStatus(String fromStation, String toStation) {
			this.fromStation = fromStation;
			this.toStation = toStation;
		}

		public String getFromStation() {
			return fromStation;
		}
		public void setFromStation(String name) {
			this.fromStation = name;
		}

		public String getToStation() {
			return toStation;
		}
		public void setToStation(String name) {
			this.toStation = name;
		}
	}
}
