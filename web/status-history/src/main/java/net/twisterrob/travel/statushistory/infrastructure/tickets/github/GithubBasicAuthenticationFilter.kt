package net.twisterrob.travel.statushistory.infrastructure.tickets.github

import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.ClientFilter
import io.micronaut.http.annotation.RequestFilter
import jakarta.inject.Inject
import net.twisterrob.travel.statushistory.infrastructure.secrets.Variables

/**
 * @see net.twisterrob.travel.statushistory.infrastructure.tickets.github.contract.GithubApiClient
 */
@ClientFilter(
	// TODO this runs on all client requests (incl. when test calls controller),
	// should be only on [GithubApiClient]. Best effort path filtering for now.
	patterns = [
		"/search/issues",
		"/repos/*/*/issues",
	]
)
class GithubBasicAuthenticationFilter @Inject constructor(
	private val variables: Variables,
) {

	@RequestFilter
	fun doFilter(request: MutableHttpRequest<*>) {
		request.basicAuth(variables.githubActor, variables.githubPat)
	}
}
