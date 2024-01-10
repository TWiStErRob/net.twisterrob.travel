package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.DescriptionChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.StatusChange
import java.util.EnumMap

class ResultChangeCalculator {

	fun diff(oldResult: Result?, newResult: Result?): ResultChange {
		val errorChange = when {
			oldResult != null && newResult != null -> diffError(oldResult, newResult)
			oldResult == null && newResult != null -> ErrorChange.NewStatus
			oldResult != null && newResult == null -> ErrorChange.LastStatus
			else /* oldResult == null && newResult == null */ -> ErrorChange.NoErrors(emptyMap())
		}
		return ResultChange(
			oldResult,
			newResult,
			errorChange,
		)
	}

	private fun diffContent(oldResult: Result.ContentResult, newResult: Result.ContentResult): Map<Line, StatusChange> {
		val statusChanges: MutableMap<Line, StatusChange> = EnumMap(Line::class.java)
		val oldMap = oldResult.content.statusMap
		val newMap = newResult.content.statusMap
		val allLines: Set<Line> = oldMap.keys + newMap.keys
		for (line in allLines) {
			val oldStatus = oldMap[line]
			val newStatus = newMap[line]
			statusChanges[line] = statusChange(oldStatus, newStatus)
		}
		return statusChanges
	}

	private fun statusChange(oldStatus: LineStatus?, newStatus: LineStatus?): StatusChange {
		if (oldStatus == null || newStatus == null) {
			return StatusChange.Unknown
		}
		val statusDiff = oldStatus.type.compareTo(newStatus.type)
		val desc = diffDesc(oldStatus, newStatus)
		return when {
			statusDiff < 0 -> StatusChange.Better(oldStatus.type, newStatus.type, desc)
			statusDiff > 0 -> StatusChange.Worse(oldStatus.type, newStatus.type, desc)
			else /* statusDiff == 0 */ -> StatusChange.Same(newStatus.type, desc)
		}
	}

	private fun diffDesc(oldStatus: LineStatus, newStatus: LineStatus): DescriptionChange {
		val oldDesc = oldStatus.description
		val newDesc = newStatus.description
		return when {
			oldDesc != null && newDesc != null -> {
				if (oldDesc != newDesc) {
					DescriptionChange.Changed(oldDesc, newDesc)
				} else {
					val oldBranches = oldStatus.branchStatuses
					val newBranches = newStatus.branchStatuses
					if (oldBranches == newBranches) {
						DescriptionChange.Same(newDesc)
					} else {
						DescriptionChange.Branches(oldBranches, newBranches)
					}
				}
			}

			oldDesc == null && newDesc != null -> {
				DescriptionChange.Added(newDesc)
			}

			oldDesc != null && newDesc == null -> {
				DescriptionChange.Removed(oldDesc)
			}

			else /* oldDesc == null && newDesc == null */ -> {
				DescriptionChange.Missing
			}
		}
	}

	private fun diffError(oldResult: Result, newResult: Result): ErrorChange {
		val oldError = (oldResult as? Result.ErrorResult)?.error
		val newError = (newResult as? Result.ErrorResult)?.error
		return when {
			oldError != null && newError != null -> {
				if (oldError.header == newError.header)
					ErrorChange.Same(newError)
				else
					ErrorChange.Change(oldError, newError)
			}

			oldError == null && newError != null -> {
				ErrorChange.Failed(newError)
			}

			oldError != null && newError == null -> {
				ErrorChange.Fixed(oldError)
			}

			else /* oldErrorHeader == null && newErrorHeader == null */ -> {
				ErrorChange.NoErrors(diffContent(oldResult as Result.ContentResult, newResult as Result.ContentResult))
			}
		}
	}
}
