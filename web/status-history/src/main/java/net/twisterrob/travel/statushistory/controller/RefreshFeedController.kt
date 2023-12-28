package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.StatusItem
import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory

@Controller
class RefreshFeedController(
	private val useCase: RefreshUseCase,
) {

	@Get("/refresh")
	@Produces(MediaType.TEXT_PLAIN)
	fun refreshFeed(
		@QueryValue("feed") feed: Feed,
	): HttpResponse<String> {
		val result = useCase.refreshLatest(feed)
		val marker = MarkerFactory.getMarker(feed.name)
		return when (result) {
			is RefreshResult.NoChange -> {
				when (result.latest) {
					is StatusItem.SuccessfulStatusItem -> {
						LOG.info(marker, "They have the same content.")
						HttpResponse.noContent()
					}

					is StatusItem.FailedStatusItem -> {
						LOG.info(marker, "They have the same error.")
						HttpResponse.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
					}
				}
			}

			is RefreshResult.Created -> {
				LOG.info(marker, "It's new, stored...")
				HttpResponse.created(result.current.retrievedDate.toString())
			}

			is RefreshResult.Refreshed -> {
				LOG.info(marker, "They're different, stored...")
				HttpResponse.accepted()
			}
		}
	}

	companion object {

		private val LOG: Logger = LoggerFactory.getLogger(RefreshFeedController::class.java)
	}
}
