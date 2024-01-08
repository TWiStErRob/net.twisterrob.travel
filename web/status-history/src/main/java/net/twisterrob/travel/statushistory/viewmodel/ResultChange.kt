package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.model.Line

open class ResultChange(
	val previous: Result?,
	val current: Result?,
	val error: ErrorChange,
	val statuses: Map<Line, StatusChange>,
	val descriptions: Map<Line, String>,
) {

	sealed class StatusChange {
		data object Better : StatusChange()
		data object Worse : StatusChange()
		data object Same : StatusChange()
		data object Unknown : StatusChange()
		data object SameDescriptionNone : StatusChange()
		data class SameDescriptionSame(val desc: String) : StatusChange()
		data class SameDescriptionChange(val oldDesc: String, val newDesc: String) : StatusChange()
		data class SameDescriptionAdd(val newDesc: String) : StatusChange()
		data class SameDescriptionDel(val oldDesc: String) : StatusChange()
		data class BranchesChange(val oldBranches: String, val newBraches: String) : StatusChange()
	}

	sealed interface ErrorChange {
		data class Same(val error: String) : ErrorChange
		data class Change(val oldError: String, val newError: String) : ErrorChange
		data class Failed(val newError: String) : ErrorChange
		data class Fixed(val oldError: String) : ErrorChange
		data object NoErrors : ErrorChange
		data object NewStatus : ErrorChange
		data object LastStatus : ErrorChange
	}
}
