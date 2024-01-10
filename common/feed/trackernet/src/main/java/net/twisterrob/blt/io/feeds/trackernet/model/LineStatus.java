package net.twisterrob.blt.io.feeds.trackernet.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

	public @Nullable String getDescription() {
		return m_description;
	}
	public void setDescription(String description) {
		if (description != null && description.trim().isEmpty()) {
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

	public @Nonnull List<BranchStatus> getBranchStatuses() {
		return Collections.unmodifiableList(m_branches);
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

		@Override public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			BranchStatus status = (BranchStatus)o;
			return Objects.equals(getFromStation(), status.getFromStation())
					&& Objects.equals(getToStation(), status.getToStation());
		}

		@Override public int hashCode() {
			return Objects.hash(getFromStation(), getToStation());
		}
	}
}
