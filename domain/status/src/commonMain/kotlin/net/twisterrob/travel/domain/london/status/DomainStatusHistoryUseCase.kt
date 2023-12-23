package net.twisterrob.travel.domain.london.status

import io.github.oshai.kotlinlogging.KMarkerFactory
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import io.github.oshai.kotlinlogging.Marker
import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusHistoryUseCase
import net.twisterrob.travel.domain.london.status.api.StatusInteractor

private val LOG = logger {}

class DomainStatusHistoryUseCase(
	private val statusHistoryRepository: StatusHistoryRepository,
	private val statusInteractor: StatusInteractor,
) : StatusHistoryUseCase {

	override fun refreshLatest(feed: Feed): RefreshResult {
		val current = statusInteractor.getCurrent(feed)
		val latest = statusHistoryRepository.getLatest(feed)
		val marker: Marker = KMarkerFactory.getMarker(feed.name)

		return if (latest != null) {
			if (sameContent(latest, current)) {
				LOG.atInfo(marker) { message = "They have the same content." }
				RefreshResult.NoChange(latest)
			} else if (sameError(latest, current)) {
				LOG.atInfo(marker) { message = "They have the same error." }
				RefreshResult.NoChange(latest)
			} else {
				LOG.atInfo(marker) { message = "They're different, storing..." }
				statusHistoryRepository.add(current)
				RefreshResult.Refreshed(current, latest)
			}
		} else {
			LOG.atInfo(marker) { message = "It's new, storing..." }
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
