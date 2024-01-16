package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.blt.model.LineStatuses
import net.twisterrob.travel.domain.london.status.Stacktrace
import net.twisterrob.travel.domain.london.status.StatusItem

sealed class ParsedStatusItem {

	abstract val item: StatusItem

	data class ParsedFeed(
		override val item: StatusItem.SuccessfulStatusItem,
		val content: LineStatuses,
	) : ParsedStatusItem()

	data class AlreadyFailed(
		override val item: StatusItem.FailedStatusItem,
	) : ParsedStatusItem()

	data class ParseFailed(
		override val item: StatusItem.SuccessfulStatusItem,
		val error: Stacktrace,
	) : ParsedStatusItem()
}
