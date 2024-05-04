package net.twisterrob.travel.statushistory.infrastructure.github

interface CreateGitHubIssueUseCase {
	fun ensureIssue(title: String, body: String)
}