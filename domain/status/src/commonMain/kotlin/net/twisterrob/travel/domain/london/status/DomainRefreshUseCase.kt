package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusDataSource

class DomainRefreshUseCase(
	private val statusHistoryDataSource: StatusHistoryDataSource,
	private val statusDataSource: StatusDataSource,
) : RefreshUseCase {

	override fun refreshLatest(feed: Feed): RefreshResult {
		val current = statusDataSource.getCurrent(feed)
		val latest = statusHistoryDataSource.getAll(feed, 1).singleOrNull()

		return when {
			latest == null -> {
				statusHistoryDataSource.add(current)
				RefreshResult.Created(current)
			}

			sameContent(latest, current) -> {
				RefreshResult.NoChange(current, latest)
			}

			sameError(latest, current) -> {
				RefreshResult.NoChange(current, latest)
			}

			else -> {
				statusHistoryDataSource.add(current)
				RefreshResult.Refreshed(current, latest)
			}
		}
	}

	private fun sameContent(latest: StatusItem, current: StatusItem): Boolean =
		if (latest is StatusItem.SuccessfulStatusItem && current is StatusItem.SuccessfulStatusItem) {
			latest.content == current.content
		} else {
			false
		}

	private fun sameError(latest: StatusItem, current: StatusItem): Boolean =
		if (latest is StatusItem.FailedStatusItem && current is StatusItem.FailedStatusItem) {
			latest.error == current.error
		} else {
			false
		}
}
