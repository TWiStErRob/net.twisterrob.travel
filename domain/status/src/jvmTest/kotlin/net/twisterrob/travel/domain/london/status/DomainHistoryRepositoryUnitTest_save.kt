package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
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
		verifyNoMoreInteractions(mockHistory, mockStatus, mockParser)
	}

	@Test fun `returns successful item`() {
		val current = SuccessfulStatusItem()

		subject.save(current)

		verify(mockHistory).add(current)
	}

	@Test fun `returns failed item`() {
		val current = FailedStatusItem()

		subject.save(current)

		verify(mockHistory).add(current)
	}
}
