package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus

class ResultChangeModelMapper {

	fun map(resultChange: ResultChange): ResultChangeModel {
		val changes = (resultChange.error as? ResultChange.ErrorChange.NoErrors)?.changes.orEmpty()
		return ResultChangeModel(
			previous = resultChange.previous,
			current = resultChange.current,
			error = map(resultChange.error),
			statuses = changes.mapValues { map(it.value) },
			descriptions = changes
				.filterValues { it is ResultChange.HasDescriptionChange }
				.mapValues { it.value as ResultChange.HasDescriptionChange }
				.mapValues { diffDesc(it.value.desc) },
		)
	}

	private fun map(resultChange: ResultChange.ErrorChange): ResultChangeModel.ErrorChange =
		when (resultChange) {
			is ResultChange.ErrorChange.Same -> ResultChangeModel.ErrorChange.Same
			is ResultChange.ErrorChange.Change -> ResultChangeModel.ErrorChange.Change
			is ResultChange.ErrorChange.Failed -> ResultChangeModel.ErrorChange.Failed
			is ResultChange.ErrorChange.Fixed -> ResultChangeModel.ErrorChange.Fixed
			is ResultChange.ErrorChange.NoErrors -> ResultChangeModel.ErrorChange.NoErrors
			ResultChange.ErrorChange.NewStatus -> ResultChangeModel.ErrorChange.NewStatus
			ResultChange.ErrorChange.LastStatus -> ResultChangeModel.ErrorChange.LastStatus
		}

	private fun map(value: ResultChange.StatusChange): ResultChangeModel.StatusChange =
		when (value) {
			is ResultChange.StatusChange.Better -> ResultChangeModel.StatusChange.Better
			is ResultChange.StatusChange.Worse -> ResultChangeModel.StatusChange.Worse
			is ResultChange.StatusChange.Same -> when (value.desc) {
				is ResultChange.DescriptionChange.Same -> ResultChangeModel.StatusChange.SameDescriptionSame
				is ResultChange.DescriptionChange.Changed -> ResultChangeModel.StatusChange.SameDescriptionChange
				is ResultChange.DescriptionChange.Added -> ResultChangeModel.StatusChange.SameDescriptionAdd
				is ResultChange.DescriptionChange.Removed -> ResultChangeModel.StatusChange.SameDescriptionDel
				is ResultChange.DescriptionChange.Branches -> ResultChangeModel.StatusChange.BranchesChange
				ResultChange.DescriptionChange.Missing -> ResultChangeModel.StatusChange.Same
			}

			ResultChange.StatusChange.Unknown -> ResultChangeModel.StatusChange.Unknown
		}

	private fun diffDesc(change: ResultChange.DescriptionChange): String =
		when (change) {
			is ResultChange.DescriptionChange.Same -> change.desc
			is ResultChange.DescriptionChange.Changed -> diffDesc(change.oldDesc, change.newDesc)
			is ResultChange.DescriptionChange.Added -> diffDesc("", change.newDesc)
			is ResultChange.DescriptionChange.Removed -> diffDesc(change.oldDesc, "")
			is ResultChange.DescriptionChange.Branches -> diffDesc(describe(change.oldBranches), describe(change.newBranches))
			ResultChange.DescriptionChange.Missing -> ""
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
