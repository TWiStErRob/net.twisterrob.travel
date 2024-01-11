package net.twisterrob.travel.domain.london.status

import io.mockative.any
import io.mockative.every
import io.mockative.mock
import io.mockative.verify
import io.mockative.verifyNoUnmetExpectations
import io.mockative.verifyNoUnverifiedExpectations
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.HistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class DomainHistoryRepositoryUnitTest_getLatest {

	private val mockHistory: StatusHistoryDataSource = mock()
	private val mockStatus: StatusDataSource = mock()
	private val mockParser: FeedParser = mock()
	private val subject: HistoryRepository = DomainHistoryRepository(mockHistory, mockStatus, mockParser)
	private val feed = Feed.TubeDepartureBoardsLineStatus

	@AfterTest
	fun verify() {
		listOf(mockHistory, mockStatus, mockParser).forEach {
			verifyNoUnverifiedExpectations(it)
			verifyNoUnmetExpectations(it)
		}
	}

	@Test fun `returns successful item`() {
		val latest = SuccessfulStatusItem()
		every { mockHistory.getAll(any(), any()) }.returns(listOf(latest))

		val result = subject.getLatest(feed)
		assertSame(latest, result)

		verify { mockHistory.getAll(feed, 1) }.wasInvoked()
	}

	@Test fun `returns failed item`() {
		val latest = FailedStatusItem()
		every { mockHistory.getAll(any(), any()) }.returns(listOf(latest))

		val result = subject.getLatest(feed)
		assertSame(latest, result)

		verify { mockHistory.getAll(feed, 1) }.wasInvoked()
	}

	@Test fun `returns no item`() {
		every { mockHistory.getAll(any(), any()) }.returns(emptyList())

		val result = subject.getLatest(feed)
		assertNull(result)

		verify { mockHistory.getAll(feed, 1) }.wasInvoked()
	}
}
