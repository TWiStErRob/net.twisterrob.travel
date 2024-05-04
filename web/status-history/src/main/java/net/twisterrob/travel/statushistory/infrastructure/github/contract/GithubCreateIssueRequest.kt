package net.twisterrob.travel.statushistory.infrastructure.github.contract

import io.micronaut.serde.annotation.Serdeable

/**
 * @see GithubApiClient.createIssue for docs.
 */
@Serdeable
class GithubCreateIssueRequest(
	val title: String,
	val body: String,
)
