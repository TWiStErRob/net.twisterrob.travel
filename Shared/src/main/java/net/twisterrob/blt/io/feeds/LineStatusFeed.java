package net.twisterrob.blt.io.feeds;

import java.util.*;

import net.twisterrob.blt.model.*;

public class LineStatusFeed extends BaseFeed {
	private List<LineStatus> m_lineStatuses;
	private Map<Line, LineStatus> m_statusMap;

	public LineStatusFeed() {
		m_lineStatuses = new LinkedList<LineStatus>();
		m_statusMap = new LinkedHashMap<Line, LineStatus>();
	}

	public Map<Line, LineStatus> getStatusMap() {
		return Collections.unmodifiableMap(m_statusMap);
	}

	public void addLineStatus(LineStatus lineStatus) {
		m_lineStatuses.add(lineStatus);
	}

	public List<LineStatus> getLineStatuses() {
		return Collections.unmodifiableList(m_lineStatuses);
	}

	@Override
	void postProcess() {
		for (LineStatus lineStatus: m_lineStatuses) {
			m_statusMap.put(lineStatus.getLine(), lineStatus);
		}
	}
}
