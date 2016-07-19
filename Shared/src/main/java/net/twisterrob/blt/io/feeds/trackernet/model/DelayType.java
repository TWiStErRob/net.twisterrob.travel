package net.twisterrob.blt.io.feeds.trackernet.model;

import java.util.Comparator;

/**
 * Possible statuses of the service a tube line.<br>
 * The names also represent the <code>CssClass</code> in the TrackerNet Line Status feed, e.g.:
 * <pre><code>&lt;Status ID="GS" CssClass="GoodService" Description="Good Service" IsActive="true"&gt;</code></pre>
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsLineStatus
 * @see net.twisterrob.blt.io.feeds.Feed#TubeDepartureBoardsLineStatusIncidents
 * @see <a href="http://cloud.tfl.gov.uk/TrackerNet/LineStatus">Current status</a>
 */
public enum DelayType {
	/**
	 * Example:
	 * <i>The Central line service has closed early to enable us to begin repairs following this evening's disruption.
	 * We are sorry for the delay to your journey.</i>
	 * TODO only seen once, GoodService may be a mistake
	 */
	ServiceClosed("SC", 90, "Service Closed", "GoodService"),
	/**
	 * TODO validate code
	 */
	Suspended("SU", 80, "Suspended", null),
	/**
	 * Example:
	 * <i>No service between Richmond and Stratford and between Willesden Junction and Clapham Junction due to
	 * a faulty train. London Underground and London Buses are accepting tickets via reasonable routes.</i>
	 */
	PartSuspended("PS", 70, "Part Suspended", "DisruptedService"),
	/**
	 * Example:
	 * <i>Train service resumes later this morning.</i>
	 */
	PlannedClosure("CS", 60, "Planned Closure", "DisruptedService"),
	/**
	 * Example:
	 * <i>No service between Wembley Park and Aldgate due to planned engineering work.
	 * GOOD SERVICE on the rest of the line.</i>
	 */
	PartClosure("PC", 50, "Part Closure", "DisruptedService"),
	/**
	 * Example:
	 * <i>Between Turnham Green and Richmond only, due to a signal failure at Richmond.
	 * GOOD SERVICE on the rest of the line.</i>
	 */
	SevereDelays("SD", 40, "Severe Delays", "DisruptedService"),
	/**
	 * TODO validate code
	 */
	ReducedService("RS", 30, "Reduced Service", null),
	/**
	 * TODO validate code
	 */
	BusService("BS", 20, "Bus Service", null),
	/**
	 * Example:
	 * <i>Between Kennington and Camden Town via Bank only, due to a person ill on a train earlier at Angel.
	 * GOOD SERVICE on the rest of the line.</i>
	 */
	MinorDelays("MD", 10, "Minor Delays", "GoodService"),
	/**
	 * Example: N/A, there's no description if everything works.
	 */
	GoodService("GS", 0, "Good Service", "GoodService"),
	Unknown("", -1, "Unknown", null);

	/**
	 * {@code ID} attribute of {@code <Status>} in the LineStatus feed.
	 */
	private final String m_code;
	/**
	 * Arbitrarily assigned ordering by me.
	 */
	private final int m_severity;
	/**
	 * {@code Description} attribute of {@code <Status>} in the LineStatus feed.
	 */
	private final String m_title;
	/**
	 * {@code CssClass} attribute of {@code <Status>} in the LineStatus feed.
	 */
	private final String m_cssClass;

	DelayType(String code, int severity, String title, String cssClass) {
		m_code = code;
		m_severity = severity;
		m_title = title;
		m_cssClass = cssClass;
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

	public String getCssClass() {
		return m_cssClass;
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
		for (DelayType lineStatus : values()) {
			if (lineStatus.m_code.equals(code)) {
				return lineStatus;
			}
		}
		return DelayType.Unknown;
	}
}
