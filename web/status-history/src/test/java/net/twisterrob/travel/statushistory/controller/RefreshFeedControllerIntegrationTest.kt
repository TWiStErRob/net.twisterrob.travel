package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase
import net.twisterrob.travel.statushistory.test.FailedStatusItem
import net.twisterrob.travel.statushistory.test.SuccessfulStatusItem
import net.twisterrob.travel.statushistory.test.assertErrorStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * @see RefreshFeedController
 */
@MicronautTest
class RefreshFeedControllerIntegrationTest {

	@Inject
	lateinit var client: BlockingHttpClient

	@MockBean(RefreshUseCase::class)
	fun refreshUseCase(): RefreshUseCase = mock()

	@Inject
	lateinit var refreshUseCase: RefreshUseCase

	@Test fun testNoFeedIsInvalidRequest() {
		val request: HttpRequest<Unit> = HttpRequest.GET("/refresh")

		client.assertErrorStatus(HttpStatus.BAD_REQUEST, request)
	}

	@Test fun testEmptyFeedIsInvalidRequest() {
		val request: HttpRequest<Unit> = HttpRequest.GET("/refresh?feed=")

		client.assertErrorStatus(HttpStatus.BAD_REQUEST, request)
	}

	@Test fun testRefresh() {
		val result = RefreshResult.Refreshed(SuccessfulStatusItem(), SuccessfulStatusItem())
		`when`(refreshUseCase.refreshLatest(Feed.TubeDepartureBoardsLineStatus)).thenReturn(result)
		val request: HttpRequest<Unit> = HttpRequest.GET("/refresh?feed=TubeDepartureBoardsLineStatus")

		val response: HttpResponse<Unit> = client.exchange(request)

		assertEquals(HttpStatus.ACCEPTED, response.status)
	}

	@Test fun testFresh() {
		val result = RefreshResult.Created(SuccessfulStatusItem())
		`when`(refreshUseCase.refreshLatest(Feed.TubeDepartureBoardsLineStatus)).thenReturn(result)
		val request: HttpRequest<Unit> = HttpRequest.GET("/refresh?feed=TubeDepartureBoardsLineStatus")

		val response: HttpResponse<String> = client.exchange(request, String::class.java)

		assertEquals(HttpStatus.CREATED, response.status)
		assertEquals(result.current.retrievedDate.toString(), response.body())
	}

	@Test fun testNoChangeInData() {
		val result = RefreshResult.NoChange(SuccessfulStatusItem(), SuccessfulStatusItem())
		`when`(refreshUseCase.refreshLatest(Feed.TubeDepartureBoardsLineStatus)).thenReturn(result)
		val request: HttpRequest<Unit> = HttpRequest.GET("/refresh?feed=TubeDepartureBoardsLineStatus")

		val response: HttpResponse<Unit> = client.exchange(request)

		assertEquals(HttpStatus.NO_CONTENT, response.status)
	}

	@Test fun testNoChangeInError() {
		val result = RefreshResult.NoChange(FailedStatusItem(), FailedStatusItem())
		`when`(refreshUseCase.refreshLatest(Feed.TubeDepartureBoardsLineStatus)).thenReturn(result)
		val request: HttpRequest<Unit> = HttpRequest.GET("/refresh?feed=TubeDepartureBoardsLineStatus")

		val response: HttpResponse<Unit> = client.exchange(request)

		assertEquals(HttpStatus.NON_AUTHORITATIVE_INFORMATION, response.status)
	}
}
