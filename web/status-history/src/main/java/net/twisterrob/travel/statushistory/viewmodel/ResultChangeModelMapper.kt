package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.ResultChangeModel.LineStatusModel

class ResultChangeModelMapper {

	fun map(changes: Changes): ResultChangeModel =
		ResultChangeModel(
			`when` = changes.current?.`when`,
			error = mapError(changes),
			statuses = map(
				(changes.current as? Result.ContentResult)?.content?.lineStatuses.orEmpty(),
				(changes as? Changes.Status)?.changes.orEmpty(),
			),
		)

	private fun mapError(changes: Changes): ResultChangeModel.ErrorChange {
		val error = (changes.current as? Result.ErrorResult)?.error
		return ResultChangeModel.ErrorChange(
			full = error?.text,
			header = error?.header,
			type = mapErrorType(changes),
		)
	}

	private fun mapErrorType(changes: Changes): ResultChangeModel.ErrorChange.Type =
		when (changes) {
			is Changes.Status -> ResultChangeModel.ErrorChange.Type.NoErrors
			Changes.Inconclusive -> ResultChangeModel.ErrorChange.Type.NoErrors
			is Changes.NewStatus -> ResultChangeModel.ErrorChange.Type.NewStatus
			is Changes.LastStatus -> ResultChangeModel.ErrorChange.Type.LastStatus
			is Changes.ErrorChanges.Same -> ResultChangeModel.ErrorChange.Type.Same
			is Changes.ErrorChanges.Change -> ResultChangeModel.ErrorChange.Type.Change
			is Changes.ErrorChanges.Failed -> ResultChangeModel.ErrorChange.Type.Failed
			is Changes.ErrorChanges.Fixed -> ResultChangeModel.ErrorChange.Type.Fixed
		}

	private fun map(value: StatusChange): ResultChangeModel.StatusChange =
		when (value) {
			is StatusChange.Better -> ResultChangeModel.StatusChange.Better
			is StatusChange.Worse -> ResultChangeModel.StatusChange.Worse
			is StatusChange.Appeared -> ResultChangeModel.StatusChange.Unknown
			is StatusChange.Disappeared -> ResultChangeModel.StatusChange.Unknown
			is StatusChange.Same -> when (value.desc) {
				is DescriptionChange.Same -> ResultChangeModel.StatusChange.SameDescriptionSame
				is DescriptionChange.Changed -> ResultChangeModel.StatusChange.SameDescriptionChange
				is DescriptionChange.Added -> ResultChangeModel.StatusChange.SameDescriptionAdd
				is DescriptionChange.Removed -> ResultChangeModel.StatusChange.SameDescriptionDel
				is DescriptionChange.Branches -> ResultChangeModel.StatusChange.BranchesChange
				DescriptionChange.Missing -> ResultChangeModel.StatusChange.Same
			}
		}

	private fun diffDesc(change: DescriptionChange): String =
		when (change) {
			is DescriptionChange.Same -> change.desc
			is DescriptionChange.Changed -> diffDesc(change.oldDesc, change.newDesc)
			is DescriptionChange.Added -> diffDesc("", change.newDesc)
			is DescriptionChange.Removed -> diffDesc(change.oldDesc, "")
			is DescriptionChange.Branches -> diffDesc(describe(change.oldBranches), describe(change.newBranches))
			DescriptionChange.Missing -> ""
		}

	private fun map(statuses: List<LineStatus>, changes: Map<Line, StatusChange>): List<LineStatusModel> =
		statuses.map { lineStatus ->
			LineStatusModel(
				line = lineStatus.line,
				type = lineStatus.type,
				description = lineStatus.description,
				changeStatus = changes[lineStatus.line]?.let(::map),
				changeDescription = (changes[lineStatus.line] as? HasDescriptionChange)?.let { diffDesc(it.desc) },
				active = lineStatus.isActive,
				branchDescription = describe(lineStatus.branchStatuses),
			)
		}

	private val Changes.current: Result?
		get() =
			when (this) {
				is Changes.Status -> current
				is Changes.NewStatus -> current
				is Changes.LastStatus -> null
				is Changes.Inconclusive -> null
				is Changes.ErrorChanges.Change -> newError
				is Changes.ErrorChanges.Failed -> newError
				is Changes.ErrorChanges.Fixed -> newResult
				is Changes.ErrorChanges.Same -> error
			}

	private fun diffDesc(oldDesc: String, newDesc: String): String =
		HtmlDiff().diff(oldDesc, newDesc)

	private fun describe(branches: List<LineStatus.BranchStatus>): String =
		buildString {
			if (branches.isNotEmpty()) {
				append("Affected branches:\n")
			}
			for (branch in branches) {
				append(branch.fromStation)
				append(" - ")
				append(branch.toStation)
				append(";\n")
			}
		}
}
