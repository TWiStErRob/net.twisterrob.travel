package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem

interface StatusHistoryDataSource {

	fun add(current: StatusItem)

	fun getAll(feed: Feed, max: Int): List<StatusItem>
}
