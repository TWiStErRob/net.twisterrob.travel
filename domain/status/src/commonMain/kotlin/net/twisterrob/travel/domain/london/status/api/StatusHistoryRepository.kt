package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.StatusItem

interface StatusHistoryRepository {

	fun getLatest(): StatusItem?
	fun add(current: StatusItem)
}
