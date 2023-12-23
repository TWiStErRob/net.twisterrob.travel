package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusHistoryUseCase
import net.twisterrob.travel.domain.london.status.api.StatusInteractor

private val LOG = logger()

class DomainStatusHistoryUseCase(
	private val statusHistoryRepository: StatusHistoryRepository,
	private val statusInteractor: StatusInteractor,
) : StatusHistoryUseCase {

	override fun refreshLatest(): RefreshResult {
		val current = statusInteractor.getCurrent()
		val latest = statusHistoryRepository.getLatest()
		val marker: org.slf4j.Marker = org.slf4j.MarkerFactory.getMarker(feed.name)

		if (latest != null) {
			if (sameContent(latest, current)) {
				LOG.info(marker, "They have the same content.")
				return RefreshResult.NoChange(latest)
			} else if (sameError(latest, current)) {
				LOG.info(marker, "They have the same error.")
				return RefreshResult.NoChange(latest)
			} else {
				LOG.info(marker, "They're different, storing...")
				statusHistoryRepository.add(current)
				return RefreshResult.Refreshed(current, latest)
			}
		} else {
			LOG.info(marker, "It's new, storing...")
			statusHistoryRepository.add(current)
			return RefreshResult.Created(current)
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
