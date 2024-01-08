package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.model.DelayType
import net.twisterrob.blt.model.Line

open class ResultChange(
	val previous: Result?,
	val current: Result?,
	val error: ErrorChange,
) {

	sealed class StatusChange {
		data class Better(
			val oldDelay: DelayType,
			val newDelay: DelayType,
			val desc: DescriptionChange,
		) : StatusChange()

		data class Worse(
			val oldDelay: DelayType,
			val newDelay: DelayType,
			val desc: DescriptionChange,
		) : StatusChange()

		data class Same(
			val delay: DelayType,
			val desc: DescriptionChange,
		) : StatusChange()

		data object Unknown : StatusChange()
	}

	sealed class DescriptionChange {
		data object None : DescriptionChange()
		data class Same(val desc: String) : DescriptionChange()
		data class Changed(val oldDesc: String, val newDesc: String) : DescriptionChange()
		data class Added(val newDesc: String) : DescriptionChange()
		data class Removed(val oldDesc: String) : DescriptionChange()
		data class Branches(val oldBranches: String, val newBranches: String) : DescriptionChange()
	}

	sealed interface ErrorChange {
		data class Same(val error: String) : ErrorChange
		data class Change(val oldError: String, val newError: String) : ErrorChange
		data class Failed(val newError: String) : ErrorChange
		data class Fixed(val oldError: String) : ErrorChange
		data class NoErrors(val changes: Map<Line, StatusChange>) : ErrorChange
		data object NewStatus : ErrorChange
		data object LastStatus : ErrorChange
	}
}
