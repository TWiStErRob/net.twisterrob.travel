package net.twisterrob.travel.statushistory.infrastructure.github.contract

import io.micronaut.http.HttpHeaders
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(id = "github") // See application.yml > micronaut.http.services.github
@Header(name = HttpHeaders.USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = HttpHeaders.ACCEPT, value = "application/vnd.github.v3+json, application/json")
interface GithubApiClient {

	/**
	 * [Rest API](https://docs.github.com/en/rest/search/search#search-issues-and-pull-requests)
	 * [Query](https://docs.github.com/en/search-github/searching-on-github/searching-issues-and-pull-requests)
	 */
	@Get("/search/issues?q=repo:${owner}/${repo}+type:issue+state:open+in:title+%22{title}%22&sort=created&order=desc")
	fun issuesWithTitle(
		@PathVariable("title") title: String,
	): GithubSearchIssuesResponse?

	/**
	 * [Rest API](https://docs.github.com/en/rest/issues/issues#create-an-issue)
	 */
	@Post("/repos/${owner}/${repo}/issues")
	fun createIssue(
		@Body body: GithubCreateIssueRequest,
	): GithubIssue?

	companion object {
		@Suppress("ConstPropertyName")
		const val owner = "\${github.feedback-repository.organization}"

		@Suppress("ConstPropertyName")
		const val repo = "\${github.feedback-repository.repo}"
	}
}
