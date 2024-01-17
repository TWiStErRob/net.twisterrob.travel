package net.twisterrob.travel.domain.london.status.changes

import com.shazam.gwen.collaborators.Arranger
import net.twisterrob.blt.model.DelayType
import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineStatus
import net.twisterrob.blt.model.LineStatuses
import java.util.Date
import java.util.EnumMap

internal class GwenStatus : Arranger {

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

	fun contains(line: Line, description: String?, vararg branches: LineStatus.BranchStatus): GwenStatus = apply {
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

	fun createResult(): Result =
		Result.ContentResult(
			`when` = Date(),
			content = LineStatuses(statuses.values.toList())
		)
}
