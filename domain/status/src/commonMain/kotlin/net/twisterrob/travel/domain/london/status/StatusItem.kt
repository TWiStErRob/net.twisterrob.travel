package net.twisterrob.travel.domain.london.status

import kotlin.time.Instant

sealed class StatusItem {

	abstract val retrievedDate: Instant
	abstract val feed: Feed

	data class SuccessfulStatusItem(
		override val feed: Feed,
		val content: StatusContent,
		override val retrievedDate: Instant,
	) : StatusItem()

	data class FailedStatusItem(
		override val feed: Feed,
		val error: Stacktrace,
		override val retrievedDate: Instant,
	) : StatusItem()
}
