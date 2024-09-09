package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainRefreshUseCaseUnitTest {

	private val mockHistory: StatusHistoryRepository = mock()
	private val subject: RefreshUseCase = DomainRefreshUseCase(mockHistory)
	private val feed = Feed.TubeDepartureBoardsLineStatus

	@AfterTest
	fun verify() {
		verifyNoMoreInteractions(mockHistory)
	}

	@Test fun `current will be saved when there are no previous statuses`() {
		val current = SuccessfulStatusItem()
		whenever(mockHistory.getCurrent(any())).thenReturn(current)
		whenever(mockHistory.getLatest(any())).thenReturn(null)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Created(current), result)

		verify(mockHistory).getCurrent(feed)
		verify(mockHistory).getLatest(feed)
		verify(mockHistory).save(current)
	}

	@Test fun `current will not be saved when it is the same as the latest status`() {
		val current = SuccessfulStatusItem()
		val latest = SuccessfulStatusItem().copy(content = current.content)
		whenever(mockHistory.getCurrent(any())).thenReturn(current)
		whenever(mockHistory.getLatest(any())).thenReturn(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.NoChange(current, latest), result)

		verify(mockHistory).getCurrent(feed)
		verify(mockHistory).getLatest(feed)
		verify(mockHistory, never()).save(any())
	}

	@Test fun `current will be saved when it differs from the latest status`() {
		val current = SuccessfulStatusItem()
		val latest = SuccessfulStatusItem()
		whenever(mockHistory.getCurrent(any())).thenReturn(current)
		whenever(mockHistory.getLatest(any())).thenReturn(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify(mockHistory).getCurrent(feed)
		verify(mockHistory).getLatest(feed)
		verify(mockHistory).save(current)
	}

	@Test fun `current error will be saved when it differs from the latest status`() {
		val current = FailedStatusItem()
		val latest = SuccessfulStatusItem()
		whenever(mockHistory.getCurrent(any())).thenReturn(current)
		whenever(mockHistory.getLatest(any())).thenReturn(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify(mockHistory).getCurrent(feed)
		verify(mockHistory).getLatest(feed)
		verify(mockHistory).save(current)
	}

	@Test fun `current success will be saved when it differs from the latest error`() {
		val current = SuccessfulStatusItem()
		val latest = FailedStatusItem()
		whenever(mockHistory.getCurrent(any())).thenReturn(current)
		whenever(mockHistory.getLatest(any())).thenReturn(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify(mockHistory).getCurrent(feed)
		verify(mockHistory).getLatest(feed)
		verify(mockHistory).save(current)
	}

	@Test fun `current error will be saved when it differs from the latest error`() {
		val current = FailedStatusItem()
		val latest = FailedStatusItem()
		whenever(mockHistory.getCurrent(any())).thenReturn(current)
		whenever(mockHistory.getLatest(any())).thenReturn(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify(mockHistory).getCurrent(feed)
		verify(mockHistory).getLatest(feed)
		verify(mockHistory).save(current)
	}

	@Test fun `current error will not be saved when it is the same as the latest error`() {
		val current = FailedStatusItem()
		val latest = FailedStatusItem().copy(error = current.error)
		whenever(mockHistory.getCurrent(any())).thenReturn(current)
		whenever(mockHistory.getLatest(any())).thenReturn(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.NoChange(current, latest), result)

		verify(mockHistory).getCurrent(feed)
		verify(mockHistory).getLatest(feed)
		verify(mockHistory, never()).save(current)
	}
}
