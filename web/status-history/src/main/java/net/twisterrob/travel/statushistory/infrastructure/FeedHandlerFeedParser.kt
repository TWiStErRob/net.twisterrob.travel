package net.twisterrob.travel.statushistory.infrastructure

import io.micronaut.context.annotation.Bean
import net.twisterrob.blt.io.feeds.FeedHandler
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusContent
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.blt.io.feeds.Feed as RawFeed

@Bean(typed = [FeedParser::class])
class FeedHandlerFeedParser : FeedParser {

	@Throws(Exception::class)
	override fun parse(feed: Feed, content: StatusContent): Any {
		content.content.byteInputStream().use { stream ->
			@Suppress("NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER") // No idea how.
			val handler: FeedHandler<*> = RawFeed.valueOf(feed.name).getHandler()
			return handler.parse(stream)
		}
	}
}
