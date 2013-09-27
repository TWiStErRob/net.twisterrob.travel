package com.twister.london.travel.model;

import java.util.Comparator;

import com.twister.london.travel.App;
import com.twister.london.travel.io.feeds.Feed;

/**
 * Possible statuses of the service a tube line.<br>
 * The names also represent the <code>CssClass</code> in the TrackerNet Line Status feed, e.g.:
 * <pre><code>&lt;Status ID="GS" CssClass="GoodService" Description="Good Service" IsActive="true"&gt;</code></pre>
 * @author TWiStEr
 * @see Feed#TubeDepartureBoardsLineStatus
 * @see Feed#TubeDepartureBoardsLineStatusIncidents
 */
public enum LineStatusType {
	Suspended("SU", 80, "Suspended"), // TODO validate code
	PartSuspended("PS", 70, "Part Suspended"), // TODO validate code
	PlannedClosure("PL", 60, "Planned Closure"), // TODO validate code
	PartClosure("PC", 50, "Part Closure"), // TODO validate code
	SevereDelays("SD", 40, "Severe Delays"),
	ReducedService("RS", 30, "Reduced Service"), // TODO validate code
	BusService("BS", 20, "Bus Service"), // TODO validate code
	MinorDelays("MD", 10, "Minor Delays"),
	GoodService("GS", 0, "Good Service"),
	Unknown("", -1, "Unknown");

	private final String m_code;
	private final int m_severity;
	private final String m_title;

	private LineStatusType(String code, int severity, String title) {
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

	public static final Comparator<LineStatusType> ORDER_SEVERITY = new Comparator<LineStatusType>() {
		public int compare(LineStatusType lhs, LineStatusType rhs) {
			return lhs.m_severity - rhs.m_severity;
		}
	};

	public static final Comparator<LineStatusType> ORDER_TITLE = new Comparator<LineStatusType>() {
		public int compare(LineStatusType lhs, LineStatusType rhs) {
			return lhs.m_title.compareTo(rhs.m_title);
		}
	};

	public static LineStatusType fromID(String attrId) {
		for (LineStatusType lineStatus: values()) {
			if (lineStatus.m_code.equals(attrId)) {
				return lineStatus;
			}
		}
		if (attrId != null) {
			App.sendMail(LineStatusType.class + " new code: " + attrId);
		}
		return LineStatusType.Unknown;
	}
}
