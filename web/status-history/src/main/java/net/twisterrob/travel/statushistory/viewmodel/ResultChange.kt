package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.model.Line
import java.util.EnumMap

open class ResultChange(
	private val oldResult: Result?,
	private val newResult: Result?,
) {

	val previous: Result?
		get() = oldResult

	val current: Result?
		get() = newResult

	private var errorChange: ErrorChange? = null
	val error: ErrorChange?
		get() = errorChange

	private val statusChanges: MutableMap<Line, StatusChange> = EnumMap(Line::class.java)
	val statuses: Map<Line, StatusChange>
		get() = statusChanges

	private val descChanges: MutableMap<Line, String> = EnumMap(Line::class.java)
	val descriptions: Map<Line, String>
		get() = descChanges

	init {
		diff()
	}

	private fun diff() {
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
	}

	protected fun diffContent() {
		if (oldResult!!.content == null || newResult!!.content == null) {
			return
		}
		val oldMap = oldResult.content!!.statusMap
		val newMap = newResult.content!!.statusMap
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

	protected fun diffDesc(line: Line, oldStatus: LineStatus, newStatus: LineStatus) {
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

	protected fun diffError() {
		val oldErrorHeader = oldResult!!.errorHeader
		val newErrorHeader = newResult!!.errorHeader
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

	enum class StatusChange(
		val title: String,
		val cssClass: String,
	) {

		Better("better", "status-better"),
		Worse("worse", "status-worse"),
		Same("", "status-same"),
		Unknown("unknown", "status-unknown"),
		SameDescriptionSame("", "status-same-desc-same"),
		SameDescriptionChange("descr.", "status-same-desc-change"),
		SameDescriptionAdd("+ descr.", "status-same-desc-add"),
		SameDescriptionDel("- descr.", "status-same-desc-del"),
		BranchesChange("branches", "status-same-branch-change"),
	}

	enum class ErrorChange(
		val title: String,
	) {

		Same("same error"),
		Change("error changed"),
		Failed("new error"),
		Fixed("error fixed"),
		NoErrors(""),
		NewStatus(""),
		LastStatus(""),
	}

	companion object {

		private fun diffDesc(oldDesc: String, newDesc: String): String =
			HtmlDiff().diff(oldDesc, newDesc)
	}
}
