package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.Stacktrace
import net.twisterrob.travel.domain.london.status.StatusItem

interface HistoryUseCase {

	fun history(feed: Feed, max: Int, includeCurrent: Boolean, includeErrors: Boolean): List<ParsedStatusItem>
}

sealed class ParsedStatusItem {

	abstract val item: StatusItem

	data class ParsedFeed(
		override val item: StatusItem.SuccessfulStatusItem,
		val content: Any,
	) : ParsedStatusItem()

	data class AlreadyFailed(
		override val item: StatusItem.FailedStatusItem,
	) : ParsedStatusItem()

	data class ParseFailed(
		override val item: StatusItem.SuccessfulStatusItem,
		val error: Stacktrace,
	) : ParsedStatusItem()
}
