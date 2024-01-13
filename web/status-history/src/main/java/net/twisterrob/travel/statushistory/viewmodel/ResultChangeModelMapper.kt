package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus

class ResultChangeModelMapper {

	fun map(changes: Changes): ResultChangeModel {
		val statusChanges = (changes as? Changes.Status)?.changes.orEmpty()
		return ResultChangeModel(
			current = changes.current,
			error = mapError(changes),
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
			Changes.None -> ResultChangeModel.ErrorChange.NoErrors
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

	private val Changes.current: Result?
		get() =
			when (this) {
				is Changes.Status -> current
				is Changes.NewStatus -> current
				is Changes.LastStatus -> null
				is Changes.None -> null
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
