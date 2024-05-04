package net.twisterrob.travel.statushistory.infrastructure.github

import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubApiClient
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubCreateIssueRequest
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubIssue
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubSearchIssuesResponse
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.kotlin.argThat
import org.mockito.kotlin.whenever

class CreateGitHubIssueInteractorUnitTest {
	private val mockClient: GithubApiClient = mock()
	private val subject = CreateGitHubIssueInteractor(mockClient)

	@Test fun `issue is only created when there are no existing issues`() {
		val searchResponse = GithubSearchIssuesResponse(0, true, emptyList())
		whenever(mockClient.issuesWithTitle("Hello"))
			.thenReturn(searchResponse)
		val createRequest = {
			argThat<GithubCreateIssueRequest> { title == "Hello" && body == "World" }
		}
		val createResponse = GithubIssue(1, "http://example.com/issue", "Hello")
		whenever(mockClient.createIssue(createRequest()))
			.thenReturn(createResponse)

		subject.ensureIssue("Hello", "World")

		verify(mockClient).createIssue(createRequest())
		verify(mockClient).issuesWithTitle("Hello")
		verifyNoMoreInteractions(mockClient)
	}

	@Test fun `issue is not created when there are existing issues`() {
		val searchResponse = GithubSearchIssuesResponse(1, false, emptyList())
		whenever(mockClient.issuesWithTitle("Hello"))
			.thenReturn(searchResponse)

		subject.ensureIssue("Hello", "World")

		verify(mockClient).issuesWithTitle("Hello")
		verifyNoMoreInteractions(mockClient)
	}

	@Test fun `issue is not created when it was already created`() {
		val searchResponse = GithubSearchIssuesResponse(0, true, emptyList())
		whenever(mockClient.issuesWithTitle("Hello"))
			.thenReturn(searchResponse)
		val createRequest = {
			argThat<GithubCreateIssueRequest> { title == "Hello" && body == "World" }
		}
		val createResponse = GithubIssue(1, "http://example.com/issue", "Hello")
		whenever(mockClient.createIssue(createRequest()))
			.thenReturn(createResponse)

		subject.ensureIssue("Hello", "World")
		subject.ensureIssue("Hello", "World")
		subject.ensureIssue("Hello", "World")

		verify(mockClient).createIssue(createRequest())
		verify(mockClient).issuesWithTitle("Hello")
		verifyNoMoreInteractions(mockClient)
	}
}
