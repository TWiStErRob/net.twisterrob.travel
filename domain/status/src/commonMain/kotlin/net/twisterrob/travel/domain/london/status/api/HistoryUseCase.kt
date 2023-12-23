package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.Stacktrace
import net.twisterrob.travel.domain.london.status.StatusItem

interface HistoryUseCase {

	fun history(feed: Feed, max: Int, current: Boolean, skipErrors: Boolean): List<ParsedStatusItem>
}

sealed class ParsedStatusItem {

	abstract val item: StatusItem

	class ParsedFeed(
		override val item: StatusItem.SuccessfulStatusItem,
		val content: Any,
	) : ParsedStatusItem()

	class AlreadyFailed(
		override val item: StatusItem.FailedStatusItem,
	) : ParsedStatusItem()

	class ParseFailed(
		override val item: StatusItem.SuccessfulStatusItem,
		val error: Stacktrace,
	) : ParsedStatusItem()
}
