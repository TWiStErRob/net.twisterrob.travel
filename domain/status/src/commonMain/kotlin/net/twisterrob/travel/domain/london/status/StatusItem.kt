package net.twisterrob.travel.domain.london.status

import kotlinx.datetime.Instant

sealed class StatusItem {

	abstract val retrievedDate: Instant

	class SuccessfulStatusItem(
		val content: StatusContent,
		override val retrievedDate: Instant,
	) : StatusItem()

	class FailedStatusItem(
		val error: Stacktrace,
		override val retrievedDate: Instant,
	) : StatusItem()
}
