package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import net.twisterrob.travel.statushistory.infrastructure.github.CreateGitHubIssueUseCase
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Controller
class InternalFeedbackController(
	private val createGitHubIssue: CreateGitHubIssueUseCase,
) {

	@Post("/InternalFeedback")
	@Consumes(MediaType.ALL)
	fun doPost(@Body body: String?) {
		val props = Properties()
		val session = Session.getDefaultInstance(props, null)
		val msg: Message = MimeMessage(session)
		try {
			msg.setFrom(InternetAddress("feedback@twisterrob-london.appspotmail.com", "BLT Internal Feedback"))
			msg.addRecipient(Message.RecipientType.TO, InternetAddress("papp.robert.s@gmail.com", "Me"))
			msg.subject = "Better London Travel automated internal feedback"
			msg.setText(body)
			Transport.send(msg)
		} catch (e: MessagingException) {
			throw IOException("Cannot send mail", e)
		}
	}

	@Get("/test")
	fun doGet() {
		LOG.info("test")
		createGitHubIssue.ensureIssue("Hello", "World")
	}

	companion object {

		private val LOG: Logger = LoggerFactory.getLogger(InternalFeedbackController::class.java)
	}
}
