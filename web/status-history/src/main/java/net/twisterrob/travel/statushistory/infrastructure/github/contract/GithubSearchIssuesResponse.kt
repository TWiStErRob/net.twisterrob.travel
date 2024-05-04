package net.twisterrob.travel.statushistory.infrastructure.github.contract

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class GithubSearchIssuesResponse(
	@Suppress("PropertyName", "detekt.ConstructorParameterNaming")
	val total_count: Int,

	@Suppress("PropertyName", "detekt.ConstructorParameterNaming")
	val incomplete_results: Boolean,

	val items: List<GithubIssue>,
)
