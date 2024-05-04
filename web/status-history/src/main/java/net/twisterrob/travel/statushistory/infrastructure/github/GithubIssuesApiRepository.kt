package net.twisterrob.travel.statushistory.infrastructure.github

import io.micronaut.context.annotation.Bean
import jakarta.inject.Inject
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubApiClient
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubCreateIssueRequest
import net.twisterrob.travel.statushistory.infrastructure.tickets.Ticket
import net.twisterrob.travel.statushistory.infrastructure.tickets.TicketsGateway

@Bean(typed = [TicketsGateway::class])
class GithubIssuesApiRepository @Inject constructor(
	private val github: GithubApiClient,
) : TicketsGateway {

	override fun searchTicketsWithTitle(title: String): List<Ticket> {
		val response = github.searchIssuesWithTitle(title)
			?: error("No response from GitHub, infrastructure error. Enable TRACE logging for HTTP client for details.")
		// Note: this is ignoring the fact that we only get 30 issues for the search.
		// This should be fine as most of the time there will be only one issue per title.
		return response.items.map { Ticket(it.html_url) }
	}

	override fun createTicket(title: String, body: String): Ticket {
		val request = GithubCreateIssueRequest(title, body, listOf("is:automated"))
		val response = github.createIssue(request)
			?: error("No response from GitHub, infrastructure error. Enable TRACE logging for HTTP client for details.")
		return Ticket(response.html_url)
	}
}
