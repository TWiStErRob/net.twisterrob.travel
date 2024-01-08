package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed

interface HistoryUseCase {

	fun history(feed: Feed, max: Int, includeCurrent: Boolean): List<ParsedStatusItem>
}
