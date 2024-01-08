package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.HistoryRepository
import net.twisterrob.travel.domain.london.status.api.HistoryUseCase
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem

class DomainHistoryUseCase(
	private val historyRepository: HistoryRepository,
) : HistoryUseCase {

	override fun history(feed: Feed, max: Int, includeCurrent: Boolean): List<ParsedStatusItem> {
		val history = historyRepository.history(feed, max, includeCurrent)
		return history
	}
}
