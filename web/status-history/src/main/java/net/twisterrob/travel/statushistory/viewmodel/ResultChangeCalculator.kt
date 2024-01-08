package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.StatusChange
import java.util.EnumMap

class ResultChangeCalculator(
	private val oldResult: Result?,
	private val newResult: Result?,
) {

	private var errorChange: ErrorChange? = null
	private val statusChanges: MutableMap<Line, StatusChange> = EnumMap(Line::class.java)
	private val descChanges: MutableMap<Line, String> = EnumMap(Line::class.java)

	fun diff(): ResultChange {
		when {
			oldResult != null && newResult != null -> {
				diffError()
				diffContent()
			}

			oldResult == null && newResult != null -> {
				errorChange = ErrorChange.NewStatus
			}

			oldResult != null && newResult == null -> {
				errorChange = ErrorChange.LastStatus
			}

			else /* oldResult == null && newResult == null */ -> {
				errorChange = ErrorChange.NoErrors
			}
		}
		return ResultChange(
			oldResult,
			newResult,
			errorChange,
			statusChanges,
			descChanges,
		)
	}

	private fun diffContent() {
		if (oldResult !is Result.ContentResult || newResult !is Result.ContentResult) {
			return
		}
		val oldMap = oldResult.content.statusMap
		val newMap = newResult.content.statusMap
		val allLines: MutableSet<Line> = HashSet()
		allLines.addAll(oldMap.keys)
		allLines.addAll(newMap.keys)
		for (line in allLines) {
			val oldStatus = oldMap[line]
			val newStatus = newMap[line]
			if (oldStatus == null || newStatus == null) {
				statusChanges[line] = StatusChange.Unknown
				continue
			}
			val statusDiff = oldStatus.type.compareTo(newStatus.type)
			when {
				statusDiff < 0 -> {
					statusChanges[line] = StatusChange.Better
				}

				statusDiff > 0 -> {
					statusChanges[line] = StatusChange.Worse
				}

				else /* statusDiff == 0 */ -> {
					statusChanges[line] = StatusChange.Same
					diffDesc(line, oldStatus, newStatus)
				}
			}
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

	private fun diffError() {
		val oldErrorHeader = (oldResult as? Result.ErrorResult)?.errorHeader
		val newErrorHeader = (newResult as? Result.ErrorResult)?.errorHeader
		errorChange = when {
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
