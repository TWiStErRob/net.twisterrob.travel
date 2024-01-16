package net.twisterrob.travel.statushistory.infrastructure

import io.micronaut.context.annotation.Bean
import net.twisterrob.blt.io.feeds.FeedHandler
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import net.twisterrob.blt.model.LineStatus
import net.twisterrob.blt.model.LineStatuses
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusContent
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.blt.io.feeds.Feed as RawFeed

@Bean(typed = [FeedParser::class])
class FeedHandlerFeedParser : FeedParser {

	@Throws(Exception::class)
	override fun parse(feed: Feed, content: StatusContent): LineStatuses {
		content.content.byteInputStream().use { stream ->
			val handler: FeedHandler<LineStatusFeed> = RawFeed.valueOf(feed.name).getHandler()
			return handler.parse(stream).toStatus()
		}
	}
}

private fun LineStatusFeed.toStatus(): LineStatuses =
	LineStatuses(
		statuses = this.lineStatuses.map { status ->
			LineStatus(
				line = status.line,
				type = status.type,
				isActive = status.isActive,
				branchStatuses = status.branchStatuses.map { branch ->
					LineStatus.BranchStatus(
						fromStation = branch.fromStation,
						toStation = branch.toStation,
					)
				},
				description = status.description,
			)
		}
	)
