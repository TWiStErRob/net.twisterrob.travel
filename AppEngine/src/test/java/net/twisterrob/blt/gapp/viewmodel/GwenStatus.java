package net.twisterrob.blt.gapp.viewmodel;

import java.util.*;

import static org.mockito.Mockito.*;

import com.shazam.gwen.collaborators.Arranger;

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.*;
import net.twisterrob.blt.model.Line;

class GwenStatus implements Arranger {
	private final LineStatusFeed feed = mock(LineStatusFeed.class);
	private final Map<Line, LineStatus> statuses = new EnumMap<>(Line.class);

	public GwenStatus contains(Line line, String description) {
		LineStatus status = new LineStatus();
		status.setLine(line);
		status.setType(DelayType.Unknown);
		status.setDescription(description);
		statuses.put(line, status);
		return this;
	}

	public GwenStatus contains(Line line, DelayType disruption) {
		LineStatus status = new LineStatus();
		status.setLine(line);
		status.setType(disruption);
		statuses.put(line, status);
		return this;
	}

	public GwenStatus doesNotContain(Line line) {
		statuses.remove(line);
		return this;
	}

	public Result createResult() {
		when(feed.getStatusMap()).thenReturn(statuses);
		return new Result(new Date(), feed);
	}
}
