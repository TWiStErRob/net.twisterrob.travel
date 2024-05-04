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
	private val subject = EnsureGitHubIssueInteractor(mockClient)

	@Test fun `issue is only created when there are no existing issues`() {
		val searchResponse = GithubSearchIssuesResponse(0, true, emptyList())
		whenever(mockClient.issuesWithTitle("My Title"))
			.thenReturn(searchResponse)
		val createRequest = {
			argThat<GithubCreateIssueRequest> { title == "My Title" && body == "Body contents." }
		}
		val createResponse = GithubIssue(1, "http://example.com/issue", "My Title")
		whenever(mockClient.createIssue(createRequest()))
			.thenReturn(createResponse)

		subject.report("My Title", "Body contents.")

		verify(mockClient).createIssue(createRequest())
		verify(mockClient).issuesWithTitle("My Title")
		verifyNoMoreInteractions(mockClient)
	}

	@Test fun `issue is not created when there are existing issues`() {
		val searchResponse = GithubSearchIssuesResponse(1, false, emptyList())
		whenever(mockClient.issuesWithTitle("My Title"))
			.thenReturn(searchResponse)

		subject.report("My Title", "Body contents.")

		verify(mockClient).issuesWithTitle("My Title")
		verifyNoMoreInteractions(mockClient)
	}

	@Test fun `issue is not created when it was already created`() {
		val searchResponse1 = GithubSearchIssuesResponse(0, true, emptyList())
		whenever(mockClient.issuesWithTitle("My Title"))
			.thenReturn(searchResponse1)
		val createRequest1 = {
			argThat<GithubCreateIssueRequest> { title == "My Title" && body == "Body contents 1." }
		}
		val createResponse1 = GithubIssue(1, "http://example.com/issue", "My Title")
		whenever(mockClient.createIssue(createRequest1()))
			.thenReturn(createResponse1)

		val searchResponse2 = GithubSearchIssuesResponse(0, true, emptyList())
		whenever(mockClient.issuesWithTitle("My Title 2"))
			.thenReturn(searchResponse2)
		val createRequest2 = {
			argThat<GithubCreateIssueRequest> { title == "My Title 2" && body == "Body contents 2." }
		}
		val createResponse = GithubIssue(1, "http://example.com/issue", "My Title")
		whenever(mockClient.createIssue(createRequest2()))
			.thenReturn(createResponse)

		subject.report("My Title", "Body contents 1.")
		subject.report("My Title 2", "Body contents 2.")
		subject.report("My Title 2", "Body contents 3.")
		subject.report("My Title", "Body contents 4.")

		verify(mockClient).createIssue(createRequest1())
		verify(mockClient).createIssue(createRequest2())
		verify(mockClient).issuesWithTitle("My Title")
		verify(mockClient).issuesWithTitle("My Title 2")
		verifyNoMoreInteractions(mockClient)
	}
}
