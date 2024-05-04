package net.twisterrob.travel.statushistory.infrastructure.tickets.github

import io.micronaut.http.MutableHttpRequest
import io.micronaut.http.annotation.ClientFilter
import io.micronaut.http.annotation.RequestFilter
import jakarta.inject.Inject
import net.twisterrob.travel.statushistory.infrastructure.secrets.Variables

/**
 * TODO this runs on all client requests, including when test is calling controllers.
 * It should be only executed on [net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubApiClient].
 *
 * @see net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubApiClient
 */
@ClientFilter
class GithubBasicAuthenticationFilter @Inject constructor(
	private val variables: Variables,
) {

	@RequestFilter
	fun doFilter(request: MutableHttpRequest<*>) {
		request.basicAuth(variables.githubActor, variables.githubPat)
	}
}
