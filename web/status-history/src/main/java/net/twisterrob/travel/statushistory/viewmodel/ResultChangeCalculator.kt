package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.DescriptionChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.StatusChange
import java.util.EnumMap

class ResultChangeCalculator {

	private val statusChanges: MutableMap<Line, StatusChange> = EnumMap(Line::class.java)

	fun diff(oldResult: Result?, newResult: Result?): ResultChange {
		statusChanges.clear()
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
		}
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
					val oldBranches = oldStatus.branchDescription
					val newBranches = newStatus.branchDescription
					if (oldBranches == newBranches) {
						DescriptionChange.Same(oldBranches)
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
				DescriptionChange.None
			}
		}
	}

	private fun diffError(oldResult: Result, newResult: Result): ErrorChange {
		val oldError = (oldResult as? Result.ErrorResult)?.fullError
		val newError = (newResult as? Result.ErrorResult)?.fullError
		return when {
			oldError != null && newError != null -> {
				if (oldError.header == newError.header)
					ErrorChange.Same(oldError)
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
				ErrorChange.NoErrors
			}
		}
	}
}

private val String.header: String
	get() = this.substringBefore('\n')
