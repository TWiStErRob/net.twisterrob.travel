package net.twisterrob.travel.statushistory.infrastructure.github

import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubApiClient
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubCreateIssueRequest
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubIssue
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubSearchIssuesResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

/**
 * @see GithubIssuesApiRepository
 */
class GithubIssuesApiRepositoryUnitTest {
	private val mockClient: GithubApiClient = mock()
	private val subject = GithubIssuesApiRepository(mockClient)

	@Test
	fun `searchIssuesWithTitle throws exception when GitHub API does not return response`() {
		val fixtTitle = "issue title"
		whenever(mockClient.searchIssuesWithTitle(fixtTitle)).thenReturn(null)

		assertThrows<IllegalStateException> {
			subject.searchTicketsWithTitle(fixtTitle)
		}
	}

	@Test
	fun `searchIssuesWithTitle returns empty list when empty response`() {
		val fixtTitle = "issue title"
		val response = GithubSearchIssuesResponse(0, false, emptyList())
		whenever(mockClient.searchIssuesWithTitle(fixtTitle)).thenReturn(response)

		val result = subject.searchTicketsWithTitle(fixtTitle)

		assertEquals(0, result.size)
	}

	@Test
	fun `searchIssuesWithTitle returns list of Issue when GitHub API returns valid response`() {
		val fixtTitle = "issue title"
		val fixtIssue = GithubIssue(123, "http://example.com/issue", "some title")
		val response = GithubSearchIssuesResponse(1, false, listOf(fixtIssue))
		whenever(mockClient.searchIssuesWithTitle(fixtTitle)).thenReturn(response)

		val result = subject.searchTicketsWithTitle(fixtTitle)

		assertEquals(1, result.size)
		assertEquals(fixtIssue.html_url, result[0].url)
	}

	@Test
	fun `createIssue throws exception when GitHub API does not return response`() {
		val fixtTitle = "issue title"
		val fixtBody = "issue body"
		val createRequest: () -> GithubCreateIssueRequest = {
			argThat { title == fixtTitle && body == fixtBody && labels == listOf("automated") }
		}
		whenever(mockClient.createIssue(createRequest())).thenReturn(null)

		assertThrows<IllegalStateException> {
			subject.createTicket(fixtTitle, fixtBody)
		}

		verify(mockClient).createIssue(createRequest())
		verifyNoMoreInteractions(mockClient)
	}

	@Test
	fun `createIssue returns Issue when GitHub API returns valid response`() {
		val fixtTitle = "issue title"
		val fixtBody = "issue body"
		val fixtUrl = "http://example.com/issue"
		val createRequest: () -> GithubCreateIssueRequest = {
			argThat { title == fixtTitle && body == fixtBody }
		}
		val response = GithubIssue(123, fixtUrl, "some title")
		whenever(mockClient.createIssue(createRequest())).thenReturn(response)

		val result = subject.createTicket(fixtTitle, fixtBody)

		assertEquals(fixtUrl, result.url)
	}
}
