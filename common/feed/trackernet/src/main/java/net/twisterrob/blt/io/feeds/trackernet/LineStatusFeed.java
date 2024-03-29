package net.twisterrob.blt.io.feeds.trackernet;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.twisterrob.blt.io.feeds.BaseFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus;
import net.twisterrob.blt.model.Line;

public class LineStatusFeed extends BaseFeed<LineStatusFeed> {
	private final List<LineStatus> m_lineStatuses = new LinkedList<>();
	private final Map<Line, LineStatus> m_statusMap = new LinkedHashMap<>();

	public Map<Line, LineStatus> getStatusMap() {
		return Collections.unmodifiableMap(m_statusMap);
	}

	void addLineStatus(LineStatus lineStatus) {
		m_lineStatuses.add(lineStatus);
	}

	public List<LineStatus> getLineStatuses() {
		return Collections.unmodifiableList(m_lineStatuses);
	}

	@Override protected void postProcess() {
		super.postProcess();
		for (LineStatus lineStatus : m_lineStatuses) {
			m_statusMap.put(lineStatus.getLine(), lineStatus);
		}
	}
}
