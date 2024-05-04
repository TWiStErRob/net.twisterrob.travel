package net.twisterrob.travel.statushistory.infrastructure.github

import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubApiClient
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubCreateIssueRequest
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubIssue
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(CreateGitHubIssueInteractor::class.java)

/**
 * See [Guide](https://guides.micronaut.io/latest/micronaut-http-client-gradle-kotlin.html).
 */
@Singleton
class CreateGitHubIssueInteractor @Inject constructor(
	private val github: GithubApiClient,
) : CreateGitHubIssueUseCase {
	override fun ensureIssue(title: String, body: String) {
		val issues = github.issuesWithTitle(title)
			?: error("No response from GitHub, infrastructure error. Enable TRACE logging for HTTP client for details.")
		if (issues.total_count == 0) { // TODO: implementation detail, move to a Repository.
			github.createIssue(GithubCreateIssueRequest(title, body))
				?: error("No response from GitHub, infrastructure error. Enable TRACE logging for HTTP client for details.")
		} else {
			val issueLinks = issues.items.map(GithubIssue::html_url).joinToString(separator = "\n")
			LOG.warn("Issue with title \"${title}\" already exists, not creating\n${body}\nExisting issue(s):\n${issueLinks}")
		}
	}
}
