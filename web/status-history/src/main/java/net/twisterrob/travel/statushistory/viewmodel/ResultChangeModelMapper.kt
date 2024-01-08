package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff

class ResultChangeModelMapper {

	fun map(resultChange: ResultChange): ResultChangeModel =
		ResultChangeModel(
			previous = resultChange.previous,
			current = resultChange.current,
			error = map(resultChange.error),
			statuses = resultChange.statuses.mapValues { map(it.value) },
			descriptions = resultChange.descriptions.mapValues { diffDesc(it.value.old, it.value.new) },
		)

	private fun map(value: ResultChange.StatusChange): ResultChangeModel.StatusChange =
		when (value) {
			ResultChange.StatusChange.Better -> ResultChangeModel.StatusChange.Better
			ResultChange.StatusChange.Worse -> ResultChangeModel.StatusChange.Worse
			ResultChange.StatusChange.Same -> ResultChangeModel.StatusChange.Same
			ResultChange.StatusChange.Unknown -> ResultChangeModel.StatusChange.Unknown
			ResultChange.StatusChange.SameDescriptionSame -> ResultChangeModel.StatusChange.SameDescriptionSame
			ResultChange.StatusChange.SameDescriptionChange -> ResultChangeModel.StatusChange.SameDescriptionChange
			ResultChange.StatusChange.SameDescriptionAdd -> ResultChangeModel.StatusChange.SameDescriptionAdd
			ResultChange.StatusChange.SameDescriptionDel -> ResultChangeModel.StatusChange.SameDescriptionDel
			ResultChange.StatusChange.BranchesChange -> ResultChangeModel.StatusChange.BranchesChange
		}

	private fun map(resultChange: ResultChange.ErrorChange): ResultChangeModel.ErrorChange =
		when (resultChange) {
			ResultChange.ErrorChange.Same -> ResultChangeModel.ErrorChange.Same
			ResultChange.ErrorChange.Change -> ResultChangeModel.ErrorChange.Change
			ResultChange.ErrorChange.Failed -> ResultChangeModel.ErrorChange.Failed
			ResultChange.ErrorChange.Fixed -> ResultChangeModel.ErrorChange.Fixed
			ResultChange.ErrorChange.NoErrors -> ResultChangeModel.ErrorChange.NoErrors
			ResultChange.ErrorChange.NewStatus -> ResultChangeModel.ErrorChange.NewStatus
			ResultChange.ErrorChange.LastStatus -> ResultChangeModel.ErrorChange.LastStatus
		}

	companion object {

		private fun diffDesc(oldDesc: String, newDesc: String): String =
			HtmlDiff().diff(oldDesc, newDesc)
	}
}
