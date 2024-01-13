package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus

class ResultChangeModelMapper {

	fun map(changes: Changes): ResultChangeModel {
		val statusChanges = (changes as? Changes.Status)?.changes.orEmpty()
		val content = changes.current as? Result.ContentResult
		val error = changes.current as? Result.ErrorResult
		return ResultChangeModel(
			`when` = content?.`when` ?: error?.`when`,
			lineStatuses = content?.content?.let(::map) ?: emptyList(),
			errorType = mapError(changes),
			errorHeader = error?.error?.header,
			fullError = error?.error?.error,
			statuses = statusChanges.mapValues { map(it.value) },
			descriptions = statusChanges
				.filterValues { it is HasDescriptionChange }
				.mapValues { it.value as HasDescriptionChange }
				.mapValues { diffDesc(it.value.desc) },
		)
	}

	private fun mapError(changes: Changes): ResultChangeModel.ErrorChange =
		when (changes) {
			is Changes.Status -> ResultChangeModel.ErrorChange.NoErrors
			Changes.Inconclusive -> ResultChangeModel.ErrorChange.NoErrors
			is Changes.NewStatus -> ResultChangeModel.ErrorChange.NewStatus
			is Changes.LastStatus -> ResultChangeModel.ErrorChange.LastStatus
			is Changes.ErrorChanges.Same -> ResultChangeModel.ErrorChange.Same
			is Changes.ErrorChanges.Change -> ResultChangeModel.ErrorChange.Change
			is Changes.ErrorChanges.Failed -> ResultChangeModel.ErrorChange.Failed
			is Changes.ErrorChanges.Fixed -> ResultChangeModel.ErrorChange.Fixed
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

	private fun map(result: LineStatusFeed): List<ResultChangeModel.LineStatusModel> =
		result.lineStatuses.map(::map)

	private fun map(status: LineStatus): ResultChangeModel.LineStatusModel =
		ResultChangeModel.LineStatusModel(
			line = status.line,
			type = status.type,
			description = status.description,
			active = status.isActive,
			branchDescription = describe(status.branchStatuses),
		)

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
