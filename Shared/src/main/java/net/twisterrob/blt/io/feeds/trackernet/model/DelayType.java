package net.twisterrob.blt.io.feeds.trackernet.model;

import java.util.Comparator;

import net.twisterrob.blt.io.feeds.Feed;

/**
 * Possible statuses of the service a tube line.<br>
 * The names also represent the <code>CssClass</code> in the TrackerNet Line Status feed, e.g.:
 * <pre><code>&lt;Status ID="GS" CssClass="GoodService" Description="Good Service" IsActive="true"&gt;</code></pre>
 * @author TWiStEr
 * @see Feed#TubeDepartureBoardsLineStatus
 * @see Feed#TubeDepartureBoardsLineStatusIncidents
 */
public enum DelayType {
	Suspended("SU", 80, "Suspended"), // TODO validate code
	PartSuspended("PS", 70, "Part Suspended"),
	PlannedClosure("CS", 60, "Planned Closure"),
	PartClosure("PC", 50, "Part Closure"),
	SevereDelays("SD", 40, "Severe Delays"),
	ReducedService("RS", 30, "Reduced Service"), // TODO validate code
	BusService("BS", 20, "Bus Service"), // TODO validate code
	MinorDelays("MD", 10, "Minor Delays"),
	GoodService("GS", 0, "Good Service"),
	Unknown("", -1, "Unknown");

	private final String m_code;
	private final int m_severity;
	private final String m_title;

	private DelayType(String code, int severity, String title) {
		m_code = code;
		m_severity = severity;
		m_title = title;
	}

	public String getCode() {
		return m_code;
	}

	public String getTitle() {
		return m_title;
	}

	public int getSeverity() {
		return m_severity;
	}

	public static final Comparator<DelayType> ORDER_SEVERITY = new Comparator<DelayType>() {
		public int compare(DelayType lhs, DelayType rhs) {
			return lhs.getSeverity() - rhs.getSeverity();
		}
	};

	public static final Comparator<DelayType> ORDER_TITLE = new Comparator<DelayType>() {
		public int compare(DelayType lhs, DelayType rhs) {
			return lhs.getTitle().compareTo(rhs.getTitle());
		}
	};

	public static DelayType fromID(String code) {
		for (DelayType lineStatus: values()) {
			if (lineStatus.m_code.equals(code)) {
				return lineStatus;
			}
		}
		return DelayType.Unknown;
	}
}
