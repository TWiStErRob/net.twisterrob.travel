package net.twisterrob.travel.statushistory.viewmodel

import com.shazam.gwen.collaborators.Arranger
import net.twisterrob.blt.model.DelayType
import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineStatus
import net.twisterrob.blt.model.LineStatus.BranchStatus
import net.twisterrob.blt.model.LineStatuses
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Date
import java.util.EnumMap

internal class GwenStatus : Arranger {

	private val feed: LineStatuses = mock()
	private val statuses: MutableMap<Line, LineStatus> = EnumMap(Line::class.java)

	fun contains(line: Line, description: String?): GwenStatus = apply {
		statuses[line] = LineStatus(
			line = line,
			type = DelayType.Unknown,
			isActive = true,
			branchStatuses = emptyList(),
			description = description,
		)
	}

	fun contains(line: Line, description: String?, vararg branches: BranchStatus): GwenStatus = apply {
		statuses[line] = LineStatus(
			line = line,
			type = DelayType.Unknown,
			isActive = true,
			description = description,
			branchStatuses = branches.toList(),
		)
	}

	fun contains(line: Line, disruption: DelayType): GwenStatus = apply {
		statuses[line] = LineStatus(
			line = line,
			type = disruption,
			isActive = true,
			description = null,
			branchStatuses = emptyList(),
		)
	}

	fun doesNotContain(line: Line): GwenStatus = apply {
		statuses.remove(line)
	}

	fun createResult(): Result {
		`when`(feed.statuses).thenReturn(statuses.values.toList())
		return Result.ContentResult(Date(), feed)
	}
}
