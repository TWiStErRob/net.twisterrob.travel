package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.model.DelayType
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.model.Line

sealed interface Difference {

	data object Nothing : Difference

	data class NewStatus(
		val current: Result,
	) : Difference

	data class LastStatus(
		val previous: Result,
	) : Difference

	data class Changes(
		val previous: Result,
		val current: Result,
		val changes: Map<Line, StatusChange>,
	) : Difference

	sealed class StatusChange {
		data class Appeared(
			val newDelay: DelayType,
		) : StatusChange()

		data class Disappeared(
			val oldDelay: DelayType,
		) : StatusChange()

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
	}

	interface HasDescriptionChange {

		val desc: DescriptionChange
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

	sealed interface ErrorDifference : Difference {
		data class Same(
			val error: Result.ErrorResult,
		) : ErrorDifference

		data class Change(
			val oldError: Result.ErrorResult,
			val newError: Result.ErrorResult,
		) : ErrorDifference

		data class Failed(
			val oldResult: Result.ContentResult,
			val newError: Result.ErrorResult,
		) : ErrorDifference

		data class Fixed(
			val oldError: Result.ErrorResult,
			val newResult: Result.ContentResult,
		) : ErrorDifference
	}
}
