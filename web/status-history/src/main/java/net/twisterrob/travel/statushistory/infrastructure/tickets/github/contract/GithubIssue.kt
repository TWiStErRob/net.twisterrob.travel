package net.twisterrob.travel.statushistory.infrastructure.tickets.github.contract

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class GithubIssue(
	val number: Int,

	@Suppress("PropertyName", "detekt.ConstructorParameterNaming")
	val html_url: String,

	val title: String,
)
