package net.twisterrob.travel.statushistory.infrastructure

import io.micronaut.context.annotation.Bean
import kotlinx.datetime.Clock
import net.twisterrob.blt.io.feeds.URLBuilder
import net.twisterrob.java.utils.ObjectTools
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.Stacktrace
import net.twisterrob.travel.domain.london.status.StatusContent
import net.twisterrob.travel.domain.london.status.StatusItem
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.HttpURLConnection
import net.twisterrob.blt.io.feeds.Feed as RawFeed

@Bean(typed = [StatusDataSource::class])
internal class HttpStatusDataSource(
	private val urlBuilder: URLBuilder,
) : StatusDataSource {

	override fun getCurrent(feed: Feed): StatusItem {
		val now = Clock.System.now()
		try {
			val content = StatusContent(downloadFeed(RawFeed.valueOf(feed.name)))
			return StatusItem.SuccessfulStatusItem(feed, content, now)
		} catch (ex: Exception) {
			LOG.error("Cannot load '{}'!", feed, ex)
			val stacktrace = Stacktrace(ObjectTools.getFullStackTrace(ex)!!)
			return StatusItem.FailedStatusItem(feed, stacktrace, now)
		}
	}

	@Throws(IOException::class)
	fun downloadFeed(feed: RawFeed?): String {
		val url = urlBuilder.getFeedUrl(feed, emptyMap<String, Any>())
		LOG.debug("Requesting feed '{}': '{}'...", feed, url)
		val connection = (url.openConnection() as HttpURLConnection)
			.apply {
				connectTimeout = 5000
				readTimeout = 5000
				connect()
			}
		return connection.inputStream.bufferedReader().use { it.readText() }
	}

	companion object {

		private val LOG: Logger = LoggerFactory.getLogger(HttpStatusDataSource::class.java)
	}
}
