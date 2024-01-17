package net.twisterrob.travel.domain.london.status

import io.mockative.mock
import io.mockative.verify
import io.mockative.verifyNoUnmetExpectations
import io.mockative.verifyNoUnverifiedExpectations
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import kotlin.test.AfterTest
import kotlin.test.Test

/**
 * @see StatusHistoryRepository.save
 */
class DomainHistoryRepositoryUnitTest_save {

	private val mockHistory: StatusHistoryDataSource = mock()
	private val mockStatus: StatusDataSource = mock()
	private val mockParser: FeedParser = mock()
	private val subject: StatusHistoryRepository = DomainStatusHistoryRepository(mockHistory, mockStatus, mockParser)

	@AfterTest
	fun verify() {
		listOf(mockHistory, mockStatus, mockParser).forEach {
			verifyNoUnverifiedExpectations(it)
			verifyNoUnmetExpectations(it)
		}
	}

	@Test fun `returns successful item`() {
		val current = SuccessfulStatusItem()

		subject.save(current)

		verify { mockHistory.add(current) }.wasInvoked()
	}

	@Test fun `returns failed item`() {
		val current = FailedStatusItem()

		subject.save(current)

		verify { mockHistory.add(current) }.wasInvoked()
	}
}
