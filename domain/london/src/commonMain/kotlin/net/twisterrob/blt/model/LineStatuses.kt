package net.twisterrob.blt.model

class LineStatuses(
	val statuses: List<LineStatus>
)

class LineStatus(
	val line: Line,
	val type: DelayType,
	val isActive: Boolean,
	val branchStatuses: List<BranchStatus>,
	val description: String?,
) {

	data class BranchStatus(
		val fromStation: String?,
		val toStation: String?,
	)
}
