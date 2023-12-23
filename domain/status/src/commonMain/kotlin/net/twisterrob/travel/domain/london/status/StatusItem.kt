package net.twisterrob.travel.domain.london.status

import kotlinx.datetime.Instant

sealed class StatusItem {

	abstract val retrievedDate: Instant
	abstract val feed: Feed

	class SuccessfulStatusItem(
		override val feed: Feed,
		val content: StatusContent,
		override val retrievedDate: Instant,
	) : StatusItem()

	class FailedStatusItem(
		override val feed: Feed,
		val error: Stacktrace,
		override val retrievedDate: Instant,
	) : StatusItem()
}
