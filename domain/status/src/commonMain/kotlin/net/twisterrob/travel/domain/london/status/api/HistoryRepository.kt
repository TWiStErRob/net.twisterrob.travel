package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem

interface HistoryRepository {

	/**
	 * Returns the top [max] items from the history of [feed] ordered by [StatusItem.retrievedDate],
	 * including the current status if [includeCurrent] is true.
	 *
	 * @param max maximum number of items to return, current is not included in the count.
	 * @param includeCurrent whether to include the current status in the result.
	 */
	fun history(feed: Feed, max: Int, includeCurrent: Boolean): List<ParsedStatusItem>

	/**
	 * @return the current live status.
	 */
	fun getCurrent(feed: Feed): StatusItem

	/**
	 * @return the latest item from history, or null if there is no history.
	 */
	fun getLatest(feed: Feed): StatusItem?

	/**
	 * Saves the item to history.
	 * If [StatusItem.retrievedDate] is later than the latest item in history, this item will become the latest.
	 *
	 * @param item the item to save.
	 */
	fun save(item: StatusItem)
}
