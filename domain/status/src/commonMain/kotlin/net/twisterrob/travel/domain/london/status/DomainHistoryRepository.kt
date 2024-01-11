package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.HistoryRepository
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource

class DomainHistoryRepository(
	private val statusHistoryDataSource: StatusHistoryDataSource,
	private val statusDataSource: StatusDataSource,
	private val feedParser: FeedParser,
) : HistoryRepository {

	override fun history(feed: Feed, max: Int, includeCurrent: Boolean): List<ParsedStatusItem> {
		val result = mutableListOf<StatusItem>()
		if (includeCurrent) {
			result.add(statusDataSource.getCurrent(feed))
		}
		val history = statusHistoryDataSource.getAll(feed, max)
		result.addAll(history)
		return result.map { it.parse() }
	}

	override fun getCurrent(feed: Feed): StatusItem =
		statusDataSource.getCurrent(feed)

	override fun getLatest(feed: Feed): StatusItem? =
		statusHistoryDataSource.getAll(feed, 1).singleOrNull()

	override fun save(item: StatusItem) {
		statusHistoryDataSource.add(item)
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
