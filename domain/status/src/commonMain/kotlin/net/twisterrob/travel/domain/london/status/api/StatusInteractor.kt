package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem

interface StatusInteractor {

	fun getCurrent(feed: Feed): StatusItem
}
