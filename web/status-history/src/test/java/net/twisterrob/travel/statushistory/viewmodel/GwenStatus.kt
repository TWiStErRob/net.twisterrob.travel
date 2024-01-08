package net.twisterrob.travel.statushistory.viewmodel

import com.shazam.gwen.collaborators.Arranger
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import net.twisterrob.blt.io.feeds.trackernet.model.DelayType
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus.BranchStatus
import net.twisterrob.blt.model.Line
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Date
import java.util.EnumMap

internal class GwenStatus : Arranger {

	private val feed: LineStatusFeed = mock()
	private val statuses: MutableMap<Line, LineStatus> = EnumMap(Line::class.java)

	fun contains(line: Line, description: String?): GwenStatus = apply {
		statuses[line] = LineStatus().apply {
			this.line = line
			this.type = DelayType.Unknown
			this.description = description
		}
	}

	fun contains(line: Line, description: String?, vararg branches: BranchStatus?): GwenStatus = apply {
		statuses[line] = LineStatus().apply {
			this.line = line
			this.type = DelayType.Unknown
			this.description = description
			for (branch in branches) {
				this.addBranchStatus(branch)
			}
		}
	}

	fun contains(line: Line, disruption: DelayType?): GwenStatus = apply {
		statuses[line] = LineStatus().apply {
			this.line = line
			this.type = disruption
		}
	}

	fun doesNotContain(line: Line): GwenStatus = apply {
		statuses.remove(line)
	}

	fun createResult(): Result {
		`when`(feed.statusMap).thenReturn(statuses)
		return Result.ContentResult(Date(), feed)
	}
}
