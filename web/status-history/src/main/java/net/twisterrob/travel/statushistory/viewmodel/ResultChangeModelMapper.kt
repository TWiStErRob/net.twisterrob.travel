package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.diff.HtmlDiff
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus

class ResultChangeModelMapper {

	fun map(difference: Difference): ResultChangeModel {
		val changes = (difference as? Difference.Changes)?.changes.orEmpty()
		return ResultChangeModel(
			previous = difference.previous,
			current = difference.current,
			error = mapError(difference),
			statuses = changes.mapValues { map(it.value) },
			descriptions = changes
				.filterValues { it is Difference.HasDescriptionChange }
				.mapValues { it.value as Difference.HasDescriptionChange }
				.mapValues { diffDesc(it.value.desc) },
		)
	}

	private fun mapError(difference: Difference): ResultChangeModel.ErrorChange =
		when (difference) {
			is Difference.Changes -> ResultChangeModel.ErrorChange.NoErrors
			Difference.Nothing -> ResultChangeModel.ErrorChange.NoErrors
			is Difference.NewStatus -> ResultChangeModel.ErrorChange.NewStatus
			is Difference.LastStatus -> ResultChangeModel.ErrorChange.LastStatus
			is Difference.ErrorDifference.Same -> ResultChangeModel.ErrorChange.Same
			is Difference.ErrorDifference.Change -> ResultChangeModel.ErrorChange.Change
			is Difference.ErrorDifference.Failed -> ResultChangeModel.ErrorChange.Failed
			is Difference.ErrorDifference.Fixed -> ResultChangeModel.ErrorChange.Fixed
		}

	private fun map(value: Difference.StatusChange): ResultChangeModel.StatusChange =
		when (value) {
			is Difference.StatusChange.Better -> ResultChangeModel.StatusChange.Better
			is Difference.StatusChange.Worse -> ResultChangeModel.StatusChange.Worse
			is Difference.StatusChange.Appeared -> ResultChangeModel.StatusChange.Unknown
			is Difference.StatusChange.Disappeared -> ResultChangeModel.StatusChange.Unknown
			is Difference.StatusChange.Same -> when (value.desc) {
				is Difference.DescriptionChange.Same -> ResultChangeModel.StatusChange.SameDescriptionSame
				is Difference.DescriptionChange.Changed -> ResultChangeModel.StatusChange.SameDescriptionChange
				is Difference.DescriptionChange.Added -> ResultChangeModel.StatusChange.SameDescriptionAdd
				is Difference.DescriptionChange.Removed -> ResultChangeModel.StatusChange.SameDescriptionDel
				is Difference.DescriptionChange.Branches -> ResultChangeModel.StatusChange.BranchesChange
				Difference.DescriptionChange.Missing -> ResultChangeModel.StatusChange.Same
			}
		}

	private fun diffDesc(change: Difference.DescriptionChange): String =
		when (change) {
			is Difference.DescriptionChange.Same -> change.desc
			is Difference.DescriptionChange.Changed -> diffDesc(change.oldDesc, change.newDesc)
			is Difference.DescriptionChange.Added -> diffDesc("", change.newDesc)
			is Difference.DescriptionChange.Removed -> diffDesc(change.oldDesc, "")
			is Difference.DescriptionChange.Branches -> diffDesc(describe(change.oldBranches), describe(change.newBranches))
			Difference.DescriptionChange.Missing -> ""
		}

	private val Difference.previous: Result?
		get() =
			when (this) {
				is Difference.Changes -> previous
				is Difference.NewStatus -> null
				is Difference.LastStatus -> previous
				is Difference.Nothing -> null
				is Difference.ErrorDifference.Change -> oldError
				is Difference.ErrorDifference.Failed -> null
				is Difference.ErrorDifference.Fixed -> oldError
				is Difference.ErrorDifference.Same -> error
			}

	private val Difference.current: Result?
		get() =
			when (this) {
				is Difference.Changes -> current
				is Difference.NewStatus -> current
				is Difference.LastStatus -> null
				is Difference.Nothing -> null
				is Difference.ErrorDifference.Change -> newError
				is Difference.ErrorDifference.Failed -> newError
				is Difference.ErrorDifference.Fixed -> newResult
				is Difference.ErrorDifference.Same -> error
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
