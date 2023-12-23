package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem

interface StatusHistoryRepository {

	fun getLatest(feed: Feed): StatusItem?
	fun add(current: StatusItem)
}
