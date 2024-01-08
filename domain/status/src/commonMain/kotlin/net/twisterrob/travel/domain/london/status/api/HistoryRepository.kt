package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed

interface HistoryRepository {

	/**
	 * @param max maximum number of items to return, current is not included in the count.
	 * @param includeCurrent whether to include the current status in the result.
	 */
	fun history(feed: Feed, max: Int, includeCurrent: Boolean): List<ParsedStatusItem>
}
