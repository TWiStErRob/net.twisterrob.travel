package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.HistoryRepository
import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase

class DomainRefreshUseCase(
	private val historyRepository: HistoryRepository,
) : RefreshUseCase {

	override fun refreshLatest(feed: Feed): RefreshResult {
		val current = historyRepository.getCurrent(feed)
		val latest = historyRepository.getLatest(feed)

		return when {
			latest == null -> {
				historyRepository.save(current)
				RefreshResult.Created(current)
			}

			sameContent(latest, current) -> {
				RefreshResult.NoChange(current, latest)
			}

			sameError(latest, current) -> {
				RefreshResult.NoChange(current, latest)
			}

			else -> {
				historyRepository.save(current)
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
