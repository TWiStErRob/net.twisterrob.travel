package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

/**
 * @see StatusHistoryRepository.getLatest
 */
class DomainHistoryRepositoryUnitTest_getLatest {

	private val mockHistory: StatusHistoryDataSource = mock()
	private val mockStatus: StatusDataSource = mock()
	private val mockParser: FeedParser = mock()
	private val subject: StatusHistoryRepository = DomainStatusHistoryRepository(mockHistory, mockStatus, mockParser)
	private val feed = Feed.TubeDepartureBoardsLineStatus

	@AfterTest
	fun verify() {
		verifyNoMoreInteractions(mockHistory, mockStatus, mockParser)
	}

	@Test fun `returns successful item`() {
		val latest = SuccessfulStatusItem()
		whenever(mockHistory.getAll(any(), any())).thenReturn(listOf(latest))

		val result = subject.getLatest(feed)
		assertSame(latest, result)

		verify(mockHistory).getAll(feed, 1)
	}

	@Test fun `returns failed item`() {
		val latest = FailedStatusItem()
		whenever(mockHistory.getAll(any(), any())).thenReturn(listOf(latest))

		val result = subject.getLatest(feed)
		assertSame(latest, result)

		verify(mockHistory).getAll(feed, 1)
	}

	@Test fun `returns no item`() {
		whenever(mockHistory.getAll(any(), any())).thenReturn(emptyList())

		val result = subject.getLatest(feed)
		assertNull(result)

		verify(mockHistory).getAll(feed, 1)
	}
}
