package net.twisterrob.travel.domain.london.status.api

import net.twisterrob.blt.model.LineStatuses
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusContent

interface FeedParser {

	@Throws(Exception::class)
	fun parse(feed: Feed, content: StatusContent): LineStatuses
}
