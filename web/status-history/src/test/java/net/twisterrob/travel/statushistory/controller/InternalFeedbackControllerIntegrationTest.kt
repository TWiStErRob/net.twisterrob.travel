package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import net.twisterrob.travel.statushistory.infrastructure.feedback.SendFeedbackUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.matches
import org.mockito.Mockito.mock
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

/**
 * @see InternalFeedbackController
 */
@MicronautTest(rebuildContext = true)
class InternalFeedbackControllerIntegrationTest {

	@Inject
	lateinit var client: BlockingHttpClient

	@MockBean(SendFeedbackUseCase::class)
	val createGitHubIssue: SendFeedbackUseCase = mock()

	@Test fun `feedback with title`() {
		val request: HttpRequest<String> = HttpRequest.POST(
			"/InternalFeedback?title=My+Title",
			"My body text.",
		)

		val response: HttpResponse<Unit> = client.exchange(request)

		assertEquals(HttpStatus.ACCEPTED, response.status)
		verify(createGitHubIssue).report(
			"My Title",
			"My body text.",
		)
		verifyNoMoreInteractions(createGitHubIssue)
	}

	@Test fun `feedback without title or body`() {
		val request: HttpRequest<Unit> = HttpRequest.POST(
			"/InternalFeedback",
			null,
		)

		val response: HttpResponse<Unit> = client.exchange(request)

		assertEquals(HttpStatus.ACCEPTED, response.status)
		verify(createGitHubIssue).report(
			matches("""^Invalid automated feedback at \d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d+Z$"""),
			eq(""),
		)
		verifyNoMoreInteractions(createGitHubIssue)
	}

	@Test fun `feedback without body`() {
		val request: HttpRequest<Unit> = HttpRequest.POST(
			"/InternalFeedback?title=My+Title",
			null,
		)

		val response: HttpResponse<Unit> = client.exchange(request)

		assertEquals(HttpStatus.ACCEPTED, response.status)
		verify(createGitHubIssue).report(
			"My Title",
			"",
		)
		verifyNoMoreInteractions(createGitHubIssue)
	}

	@Test fun `feedback without title`() {
		val request: HttpRequest<String> = HttpRequest.POST(
			"/InternalFeedback",
			"My body text.",
		)

		val response: HttpResponse<Unit> = client.exchange(request)

		assertEquals(HttpStatus.ACCEPTED, response.status)
		verify(createGitHubIssue).report(
			matches("""^Invalid automated feedback at \d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d+Z$"""),
			eq("My body text."),
		)
		verifyNoMoreInteractions(createGitHubIssue)
	}
}
