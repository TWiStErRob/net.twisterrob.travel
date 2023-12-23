package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.StatusItem

interface StatusHistoryUseCase {

	fun refreshLatest(): RefreshResult
}

sealed class RefreshResult {
	class Created(current: StatusItem) : RefreshResult()
	class Refreshed(current: StatusItem, latest: StatusItem) : RefreshResult()
	class NoChange(latest: StatusItem) : RefreshResult()
}
