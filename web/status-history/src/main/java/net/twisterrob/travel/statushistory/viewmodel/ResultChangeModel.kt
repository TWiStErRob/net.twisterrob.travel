package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.model.DelayType
import net.twisterrob.blt.model.Line
import java.util.Date

open class ResultChangeModel(
	val `when`: Date?,
	val lineStatuses: List<LineStatusModel>,
	val errorType: ErrorChange?,
	val errorHeader: String?,
	val fullError: String?,
	val statuses: Map<Line, StatusChange>,
	val descriptions: Map<Line, String>,
) {

	class LineStatusModel(
		val line: Line,
		val type: DelayType,
		val description: String?,
		val active: Boolean,
		val branchDescription: String?,
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
}
