package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem

interface StatusHistoryUseCase {

	fun refreshLatest(feed: Feed): RefreshResult
}

sealed class RefreshResult {
	class Created(current: StatusItem) : RefreshResult()
	class Refreshed(current: StatusItem, latest: StatusItem) : RefreshResult()
	class NoChange(latest: StatusItem) : RefreshResult()
}
