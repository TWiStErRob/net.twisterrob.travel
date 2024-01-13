package net.twisterrob.travel.domain.london.status

import io.mockative.any
import io.mockative.every
import io.mockative.mock
import io.mockative.verify
import io.mockative.verifyNoUnmetExpectations
import io.mockative.verifyNoUnverifiedExpectations
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertSame

class DomainHistoryRepositoryUnitTest_getCurrent {

	private val mockHistory: StatusHistoryDataSource = mock()
	private val mockStatus: StatusDataSource = mock()
	private val mockParser: FeedParser = mock()
	private val subject: StatusHistoryRepository = DomainStatusHistoryRepository(mockHistory, mockStatus, mockParser)
	private val feed = Feed.TubeDepartureBoardsLineStatus

	@AfterTest
	fun verify() {
		listOf(mockHistory, mockStatus, mockParser).forEach {
			verifyNoUnverifiedExpectations(it)
			verifyNoUnmetExpectations(it)
		}
	}

	@Test fun `returns successful item`() {
		val current = SuccessfulStatusItem()
		every { mockStatus.getCurrent(any()) }.returns(current)

		val result = subject.getCurrent(feed)
		assertSame(current, result)

		verify { mockStatus.getCurrent(feed) }.wasInvoked()
	}

	@Test fun `returns failed item`() {
		val current = FailedStatusItem()
		every { mockStatus.getCurrent(any()) }.returns(current)

		val result = subject.getCurrent(feed)
		assertSame(current, result)

		verify { mockStatus.getCurrent(feed) }.wasInvoked()
	}
}
