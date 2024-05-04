package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import kotlinx.datetime.Clock
import net.twisterrob.travel.statushistory.infrastructure.github.SendFeedbackUseCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller
class InternalFeedbackController(
	private val createGitHubIssue: SendFeedbackUseCase,
) {

	@Post("/InternalFeedback")
	@Consumes(MediaType.ALL)
	fun doPost(@QueryValue("title") title: String?, @Body body: String?): HttpResponse<String> {
		LOG.debug("Received feedback: ${title}\n${body}")
		createGitHubIssue.report(title ?: fallbackTitle(), body ?: "")
		return HttpResponse.accepted()
	}

	private fun fallbackTitle(): String =
		"Invalid automated feedback at ${Clock.System.now()}"

	companion object {

		private val LOG: Logger = LoggerFactory.getLogger(InternalFeedbackController::class.java)
	}
}
