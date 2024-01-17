@file:Suppress("unused", "CanBeParameter", "MemberVisibilityCanBePrivate") // Used by LineStatus.hbs.

package net.twisterrob.travel.statushistory.viewmodel

import java.util.Date

class LineStatusModel(
	val feedChanges: List<ResultChangeModel>,
	val colors: List<LineColorsModel>,
)

class ResultChangeModel(
	val `when`: Date?,
	val statuses: List<LineStatusModel>,
	val error: ErrorChange?,
) {

	class LineStatusModel(
		val lineId: String,
		val lineTitle: String,
		val delayType: String,
		val description: String?,
		val active: Boolean,
		val branchDescription: String?,
		val changeStatus: StatusChange?,
		val changeDescription: String?,
	)

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
		;

		init {
			require(cssClass.isNotEmpty()) { "CSS class must not be empty: ${this} has cssClass=${cssClass}." }
		}
	}

	class ErrorChange(
		val type: Type,
		val header: String?,
		val full: String?,
	) {

		enum class Type(
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
	}
}
