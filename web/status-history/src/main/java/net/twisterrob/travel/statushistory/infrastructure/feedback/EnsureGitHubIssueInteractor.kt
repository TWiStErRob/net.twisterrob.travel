package net.twisterrob.travel.statushistory.infrastructure.feedback

import io.micronaut.context.annotation.Requires
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.twisterrob.travel.statushistory.infrastructure.tickets.Ticket
import net.twisterrob.travel.statushistory.infrastructure.tickets.TicketsGateway
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

private val LOG = LoggerFactory.getLogger(EnsureGitHubIssueInteractor::class.java)

/**
 * See [Guide](https://guides.micronaut.io/latest/micronaut-http-client-gradle-kotlin.html).
 */
@Singleton
@Requires(env = ["production"])
class EnsureGitHubIssueInteractor @Inject constructor(
	private val issuesGateway: TicketsGateway,
) : SendFeedbackUseCase {

	/**
	 * Keep state to reduce the chances of creating the same issue multiple times.
	 * For some reason the search API does not return the "just created" issues.
	 */
	private val alreadyCreated: MutableSet<String> = ConcurrentHashMap.newKeySet()

	override fun report(title: String, body: String) {
		if (title in alreadyCreated) return
		val issues = issuesGateway.searchTicketsWithTitle(title)
		if (issues.isEmpty()) {
			val issue = issuesGateway.createTicket(title, body)
			LOG.info("Issue created for \"${title}\": ${issue.url}")
			alreadyCreated.add(title)
		} else {
			val issueLinks = issues.map(Ticket::url).joinToString(separator = "\n")
			LOG.warn("Issue with title \"${title}\" already exists, not creating\n${body}\nExisting issue(s):\n${issueLinks}")
		}
	}
}
