package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.model.DelayType
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.model.Line

open class ResultChange(
	val previous: Result?,
	val current: Result?,
	val error: ErrorChange, // STOPSHIP seal for error and changes
) {

	interface HasDescriptionChange {

		val desc: DescriptionChange
	}

	sealed class StatusChange {
		data class Better(
			val oldDelay: DelayType,
			val newDelay: DelayType,
			override val desc: DescriptionChange,
		) : StatusChange(), HasDescriptionChange

		data class Worse(
			val oldDelay: DelayType,
			val newDelay: DelayType,
			override val desc: DescriptionChange,
		) : StatusChange(), HasDescriptionChange

		data class Same(
			val delay: DelayType,
			override val desc: DescriptionChange,
		) : StatusChange(), HasDescriptionChange

		data object Unknown : StatusChange()
	}

	sealed class DescriptionChange {
		data object Missing : DescriptionChange()
		data class Same(
			val desc: String,
		) : DescriptionChange()

		data class Changed(
			val oldDesc: String,
			val newDesc: String,
		) : DescriptionChange()

		data class Added(
			val newDesc: String,
		) : DescriptionChange()

		data class Removed(
			val oldDesc: String,
		) : DescriptionChange()

		data class Branches(
			val oldBranches: List<LineStatus.BranchStatus>,
			val newBranches: List<LineStatus.BranchStatus>,
		) : DescriptionChange()
	}

	sealed interface ErrorChange {
		data class Same(
			val error: Result.ErrorResult.Error,
		) : ErrorChange

		data class Change(
			val oldError: Result.ErrorResult.Error,
			val newError: Result.ErrorResult.Error,
		) : ErrorChange

		data class Failed(
			val newError: Result.ErrorResult.Error,
		) : ErrorChange

		data class Fixed(
			val oldError: Result.ErrorResult.Error,
		) : ErrorChange

		data class NoErrors(
			val changes: Map<Line, StatusChange>,
		) : ErrorChange

		data object NewStatus : ErrorChange
		data object LastStatus : ErrorChange
	}
}
