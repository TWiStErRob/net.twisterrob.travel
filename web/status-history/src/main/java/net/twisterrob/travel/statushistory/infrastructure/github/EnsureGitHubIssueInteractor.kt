package net.twisterrob.travel.statushistory.infrastructure.github

import io.micronaut.context.annotation.Requires
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubApiClient
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubCreateIssueRequest
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubIssue
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

private val LOG = LoggerFactory.getLogger(EnsureGitHubIssueInteractor::class.java)

/**
 * See [Guide](https://guides.micronaut.io/latest/micronaut-http-client-gradle-kotlin.html).
 */
@Singleton
@Requires(env = ["production"])
class EnsureGitHubIssueInteractor @Inject constructor(
	private val github: GithubApiClient,
) : SendFeedbackUseCase {

	/**
	 * Keep state to reduce the chances of creating the same issue multiple times.
	 * For some reason the search API does not return the "just created" issues.
	 */
	private val alreadyCreated: MutableSet<String> = ConcurrentHashMap.newKeySet()

	override fun report(title: String, body: String) {
		if (title in alreadyCreated) return
		val issues = github.issuesWithTitle(title)
			?: error("No response from GitHub, infrastructure error. Enable TRACE logging for HTTP client for details.")
		if (issues.total_count == 0) { // TODO: implementation detail, move to a Repository.
			github.createIssue(GithubCreateIssueRequest(title, body))
				?: error("No response from GitHub, infrastructure error. Enable TRACE logging for HTTP client for details.")
			alreadyCreated.add(title)
		} else {
			val issueLinks = issues.items.map(GithubIssue::html_url).joinToString(separator = "\n")
			LOG.warn("Issue with title \"${title}\" already exists, not creating\n${body}\nExisting issue(s):\n${issueLinks}")
		}
	}
}
