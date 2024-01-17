package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem
import net.twisterrob.travel.domain.london.status.changes.Changes

interface StatusHistoryUseCase {

	/**
	 * Returns the top [max] items from the history of [feed] ordered by [StatusItem.retrievedDate],
	 * including the current status if [includeCurrent] is true.
	 *
	 * @param max maximum number of items to return, current is not included in the count.
	 * @param includeCurrent whether to include the current status in the result.
	 * @param includeErrors whether to include errors in the result.
	 */
	fun history(feed: Feed, max: Int, includeCurrent: Boolean, includeErrors: Boolean): List<Changes>
}
