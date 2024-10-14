package net.twisterrob.blt.io.feeds

import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData
import net.twisterrob.blt.model.Line
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale

/**
 * From email: Thank you for registering for Transport for London (TfL) syndicated feeds.
 */
class TFLUrlBuilder(
	private val email: String,
	private val trackerNetData: TrackerNetData,
) : URLBuilder {

	@Throws(MalformedURLException::class)
	fun getSyncdicationFeed(feedId: Int): URL {
		val query = "?email=%s&feedId=%d".format(Locale.ROOT, email, feedId)
		return URL(Feed.Type.Syndication.baseUrl, query)
	}

	@Throws(MalformedURLException::class)
	fun getSyncdicationFeed(feed: Feed): URL {
		require(feed.type == Feed.Type.Syndication) {
			"Only syndication feeds are allowed here, got ${feed}."
		}
		return getSyncdicationFeed(feed.feedId)
	}

	@Throws(MalformedURLException::class)
	override fun getFeedUrl(feed: Feed, args: Map<String, *>): URL {
		if (feed.type == Feed.Type.Syndication) {
			return getSyncdicationFeed(feed)
		}
		when (feed) {
			Feed.TubeDepartureBoardsPredictionSummary -> {
				val line = args["line"] as Line
				return URL(feed.url, trackerNetData.getTrackerNetCodeOf(line))
			}

			Feed.TubeDepartureBoardsPredictionDetailed -> {
				val line = args["line"] as Line
				require(line in Line.UNDERGROUND) {
					"${line} line is not Underground, cannot request prediction for it."
				}
				val code = trackerNetData.getTrackerNetCodeOf(line)
				return URL(URL(feed.url, "$code/"), args["station"] as String)
			}

			else -> {
				return feed.url
			}
		}
	}
}
