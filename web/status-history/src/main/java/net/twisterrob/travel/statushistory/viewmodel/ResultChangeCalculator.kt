package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.StatusChange
import java.util.EnumMap

class ResultChangeCalculator {

	private val statusChanges: MutableMap<Line, StatusChange> = EnumMap(Line::class.java)
	private val descChanges: MutableMap<Line, String> = EnumMap(Line::class.java)

	fun diff(oldResult: Result?, newResult: Result?): ResultChange {
		statusChanges.clear()
		descChanges.clear()
		val errorChange = when {
			oldResult != null && newResult != null -> diffError(oldResult, newResult)
			oldResult == null && newResult != null -> ErrorChange.NewStatus
			oldResult != null && newResult == null -> ErrorChange.LastStatus
			else /* oldResult == null && newResult == null */ -> ErrorChange.NoErrors
		}
		if (errorChange == ErrorChange.NoErrors) {
			diffContent(oldResult as Result.ContentResult, newResult as Result.ContentResult)
		}
		return ResultChange(
			oldResult,
			newResult,
			errorChange,
			statusChanges,
			descChanges,
		)
	}

	private fun diffContent(oldResult: Result.ContentResult, newResult: Result.ContentResult) {
		val oldMap = oldResult.content.statusMap
		val newMap = newResult.content.statusMap
		val allLines: MutableSet<Line> = HashSet()
		allLines.addAll(oldMap.keys)
		allLines.addAll(newMap.keys)
		for (line in allLines) {
			val oldStatus = oldMap[line]
			val newStatus = newMap[line]
			statusChanges[line] = statusChange(oldStatus, newStatus)
			if (statusChanges[line] == StatusChange.Same) {
				diffDesc(line, oldStatus!!, newStatus!!)
			}
		}
	}

	private fun statusChange(oldStatus: LineStatus?, newStatus: LineStatus?): StatusChange {
		if (oldStatus == null || newStatus == null) {
			return StatusChange.Unknown
		}
		val statusDiff = oldStatus.type.compareTo(newStatus.type)
		return when {
			statusDiff < 0 -> StatusChange.Better
			statusDiff > 0 -> StatusChange.Worse
			else /* statusDiff == 0 */ -> StatusChange.Same
		}
	}

	private fun diffDesc(line: Line, oldStatus: LineStatus, newStatus: LineStatus) {
		val oldDesc = oldStatus.description
		val newDesc = newStatus.description
		when {
			oldDesc != null && newDesc != null -> {
				if (oldDesc != newDesc) {
					statusChanges[line] = StatusChange.SameDescriptionChange
					descChanges[line] = diffDesc(oldDesc, newDesc)
				} else {
					val oldBranches = oldStatus.branchDescription
					val newBranches = newStatus.branchDescription
					if (oldBranches == newBranches) {
						statusChanges[line] = StatusChange.SameDescriptionSame
					} else {
						statusChanges[line] = StatusChange.BranchesChange
						descChanges[line] = diffDesc(oldBranches, newBranches)
					}
				}
			}

			oldDesc == null && newDesc != null -> {
				statusChanges[line] = StatusChange.SameDescriptionAdd
				descChanges[line] = diffDesc("", newDesc)
			}

			oldDesc != null && newDesc == null -> {
				statusChanges[line] = StatusChange.SameDescriptionDel
				descChanges[line] = diffDesc(oldDesc, "")
			}

			else /* oldDesc == null && newDesc == null */ -> {
				statusChanges[line] = StatusChange.SameDescriptionSame
			}
		}
	}

	private fun diffError(oldResult: Result?, newResult: Result?): ErrorChange {
		val oldErrorHeader = (oldResult as? Result.ErrorResult)?.errorHeader
		val newErrorHeader = (newResult as? Result.ErrorResult)?.errorHeader
		return when {
			oldErrorHeader != null && newErrorHeader != null -> {
				if (oldErrorHeader == newErrorHeader) ErrorChange.Same else ErrorChange.Change
			}

			oldErrorHeader == null && newErrorHeader != null -> {
				ErrorChange.Failed
			}

			oldErrorHeader != null && newErrorHeader == null -> {
				ErrorChange.Fixed
			}

			else /* oldErrorHeader == null && newErrorHeader == null */ -> {
				ErrorChange.NoErrors
			}
		}
	}

	companion object {

		private fun diffDesc(oldDesc: String, newDesc: String): String =
			HtmlDiff().diff(oldDesc, newDesc)
	}
}
