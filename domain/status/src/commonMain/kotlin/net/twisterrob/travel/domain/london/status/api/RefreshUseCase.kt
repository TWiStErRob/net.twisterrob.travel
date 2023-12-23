package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem

interface RefreshUseCase {

	fun refreshLatest(feed: Feed): RefreshResult
}

sealed class RefreshResult {
	class Created(
		val current: StatusItem
	) : RefreshResult()

	class Refreshed(
		val current: StatusItem,
		val latest: StatusItem
	) : RefreshResult()

	class NoChange(
		val latest: StatusItem
	) : RefreshResult()
}
