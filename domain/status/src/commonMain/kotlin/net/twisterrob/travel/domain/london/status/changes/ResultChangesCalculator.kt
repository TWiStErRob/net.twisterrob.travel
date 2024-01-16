package net.twisterrob.travel.domain.london.status.changes

import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineStatus
import java.util.EnumMap

class ResultChangesCalculator {

	fun getChanges(results: List<Result>): List<Changes> {
		val changes: MutableList<Changes> = ArrayList(results.size)
		var newResult: Result? = null
		for (oldResult in results) { // We're going forward, but the list is backwards.
			changes.add(diff(oldResult, newResult))
			newResult = oldResult
		}
		changes.add(diff(null, newResult))
		changes.removeAt(0)
		return changes
	}

	fun diff(oldResult: Result?, newResult: Result?): Changes =
		when {
			oldResult == null && newResult != null -> Changes.NewStatus(newResult)
			oldResult != null && newResult == null -> Changes.LastStatus(oldResult)
			oldResult != null && newResult != null -> diffResults(oldResult, newResult)
			else /* oldResult == null && newResult == null */ -> Changes.Inconclusive
		}

	private fun diffResults(oldResult: Result, newResult: Result): Changes =
		when {
			oldResult is Result.ContentResult && newResult is Result.ContentResult ->
				Changes.Status(oldResult, newResult, diffContent(oldResult, newResult))

			oldResult is Result.ErrorResult || newResult is Result.ErrorResult ->
				diffError(oldResult, newResult)

			else ->
				error("Unknown combination of results: old=${oldResult}, new=${newResult}.")
		}

	private fun diffContent(oldResult: Result.ContentResult, newResult: Result.ContentResult): Map<Line, StatusChange> {
		val statusChanges: MutableMap<Line, StatusChange> = EnumMap(Line::class.java)
		val oldMap = oldResult.content.statuses.associateBy { it.line }
		val newMap = newResult.content.statuses.associateBy { it.line }
		val allLines: Set<Line> = oldMap.keys + newMap.keys
		for (line in allLines) {
			val oldStatus = oldMap[line]
			val newStatus = newMap[line]
			statusChanges[line] = statusChange(oldStatus, newStatus)
		}
		return statusChanges
	}

	private fun statusChange(oldStatus: LineStatus?, newStatus: LineStatus?): StatusChange =
		when {
			oldStatus == null && newStatus == null -> error("Missing statuses")
			oldStatus == null && newStatus != null -> StatusChange.Appeared(newStatus.type)
			oldStatus != null && newStatus == null -> StatusChange.Disappeared(oldStatus.type)

			oldStatus != null && newStatus != null -> {
				val statusDiff = oldStatus.type.compareTo(newStatus.type)
				val desc = diffDesc(oldStatus, newStatus)
				when {
					statusDiff > 0 -> StatusChange.Better(oldStatus.type, newStatus.type, desc)
					statusDiff < 0 -> StatusChange.Worse(oldStatus.type, newStatus.type, desc)
					else /* statusDiff == 0 */ -> StatusChange.Same(newStatus.type, desc)
				}
			}

			else -> error("Unknown combination of statuses: old=${oldStatus}, new=${newStatus}.")
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

			oldDesc == null && newDesc != null -> DescriptionChange.Added(newDesc)
			oldDesc != null && newDesc == null -> DescriptionChange.Removed(oldDesc)
			else /* oldDesc == null && newDesc == null */ -> DescriptionChange.Missing
		}
	}

	private fun diffError(oldResult: Result, newResult: Result): Changes {
		val oldError = (oldResult as? Result.ErrorResult)?.error
		val newError = (newResult as? Result.ErrorResult)?.error
		return when {
			oldError != null && newError != null -> {
				if (oldError.header == newError.header)
					Changes.ErrorChanges.Same(newResult)
				else
					Changes.ErrorChanges.Change(oldResult, newResult)
			}

			oldError == null && newError != null -> {
				Changes.ErrorChanges.Failed(oldResult as Result.ContentResult, newResult)
			}

			oldError != null && newError == null -> {
				Changes.ErrorChanges.Fixed(oldResult, newResult as Result.ContentResult)
			}

			else /* oldError == null && newError == null */ -> {
				error("Neither result has an error: ${oldResult}, ${newResult}")
			}
		}
	}
}
