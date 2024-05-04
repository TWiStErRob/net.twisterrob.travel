package net.twisterrob.travel.statushistory.infrastructure.tickets.github.contract

import io.micronaut.serde.annotation.Serdeable

/**
 * @see GithubApiClient.createIssue for docs.
 */
@Serdeable
class GithubCreateIssueRequest(
	val title: String,
	val body: String,
)
