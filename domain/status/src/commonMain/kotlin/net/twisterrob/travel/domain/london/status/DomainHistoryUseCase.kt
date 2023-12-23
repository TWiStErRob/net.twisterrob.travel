package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.HistoryUseCase
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusInteractor

class DomainHistoryUseCase(
	private val statusHistoryRepository: StatusHistoryRepository,
	private val statusInteractor: StatusInteractor,
	private val feedParser: FeedParser,
) : HistoryUseCase {

	/**
	 * @param max maximum number of items to return, current is not included in the count.
	 * @param includeCurrent whether to include the current status in the result.
	 */
	override fun history(feed: Feed, max: Int, includeCurrent: Boolean): List<ParsedStatusItem> {
		val result = mutableListOf<StatusItem>()
		if (includeCurrent) {
			result.add(statusInteractor.getCurrent(feed))
		}
		val history = statusHistoryRepository.fetchEntries(feed, max)
		result.addAll(history)
		return result.map { it.parse() }
	}

	private fun StatusItem.parse(): ParsedStatusItem =
		when (this) {
			is StatusItem.SuccessfulStatusItem -> {
				try {
					val feedContents = feedParser.parse(this.feed, this.content)
					ParsedStatusItem.ParsedFeed(this, feedContents)
				} catch (ex: Exception) {
					ParsedStatusItem.ParseFailed(this, Stacktrace(ex.stackTraceToString()))
				}
			}

			is StatusItem.FailedStatusItem -> {
				ParsedStatusItem.AlreadyFailed(this)
			}
		}
}
