package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem

interface StatusHistoryRepository {

	fun add(current: StatusItem)

	fun fetchEntries(feed: Feed, max: Int): List<StatusItem>
}
