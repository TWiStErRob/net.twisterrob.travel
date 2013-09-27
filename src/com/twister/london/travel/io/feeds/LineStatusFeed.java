package com.twister.london.travel.io.feeds;

import java.util.*;

import com.twister.london.travel.model.*;

public class LineStatusFeed extends BaseFeed {
	private Map<Line, LineStatus> m_lineStatuses;

	public LineStatusFeed() {
		m_lineStatuses = new HashMap<Line, LineStatus>();
	}

	public Map<Line, LineStatus> getLineStatuses() {
		return m_lineStatuses;
	}

	public void postProcess() {

	}
}