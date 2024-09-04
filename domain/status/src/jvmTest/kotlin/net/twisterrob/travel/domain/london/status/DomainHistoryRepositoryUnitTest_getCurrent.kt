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
import kotlin.test.assertSame

/**
 * @see StatusHistoryRepository.getCurrent
 */
class DomainHistoryRepositoryUnitTest_getCurrent {

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
		val current = SuccessfulStatusItem()
		whenever(mockStatus.getCurrent(any())).thenReturn(current)

		val result = subject.getCurrent(feed)
		assertSame(current, result)

		verify(mockStatus).getCurrent(feed)
	}

	@Test fun `returns failed item`() {
		val current = FailedStatusItem()
		whenever(mockStatus.getCurrent(any())).thenReturn(current)

		val result = subject.getCurrent(feed)
		assertSame(current, result)

		verify(mockStatus).getCurrent(feed)
	}
}
