package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusHistoryUseCase
import net.twisterrob.travel.domain.london.status.api.StatusInteractor

class DomainStatusHistoryUseCase(
	private val statusHistoryRepository: StatusHistoryRepository,
	private val statusInteractor: StatusInteractor,
) : StatusHistoryUseCase {

	override fun refreshLatest(feed: Feed): RefreshResult {
		val current = statusInteractor.getCurrent(feed)
		val latest = statusHistoryRepository.getLatest(feed)

		return if (latest != null) {
			if (sameContent(latest, current)) {
				RefreshResult.NoChange(latest)
			} else if (sameError(latest, current)) {
				RefreshResult.NoChange(latest)
			} else {
				statusHistoryRepository.add(current)
				RefreshResult.Refreshed(current, latest)
			}
		} else {
			statusHistoryRepository.add(current)
			RefreshResult.Created(current)
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