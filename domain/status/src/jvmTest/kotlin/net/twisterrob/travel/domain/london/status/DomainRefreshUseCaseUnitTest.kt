package net.twisterrob.travel.domain.london.status

import io.mockative.any
import io.mockative.every
import io.mockative.mock
import io.mockative.verify
import io.mockative.verifyNoUnmetExpectations
import io.mockative.verifyNoUnverifiedExpectations
import net.twisterrob.travel.domain.london.status.api.RefreshResult
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainRefreshUseCaseUnitTest {

	private val mockHistory: StatusHistoryRepository = mock()
	private val subject: RefreshUseCase = DomainRefreshUseCase(mockHistory)
	private val feed = Feed.TubeDepartureBoardsLineStatus

	@AfterTest
	fun verify() {
		listOf(mockHistory).forEach {
			verifyNoUnverifiedExpectations(it)
			verifyNoUnmetExpectations(it)
		}
	}

	@Test fun `current will be saved when there are no previous statuses`() {
		val current = SuccessfulStatusItem()
		every { mockHistory.getCurrent(any()) }.returns(current)
		every { mockHistory.getLatest(any()) }.returns(null)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Created(current), result)

		verify { mockHistory.getCurrent(feed) }.wasInvoked()
		verify { mockHistory.getLatest(feed) }.wasInvoked()
		verify { mockHistory.save(current) }.wasInvoked()
	}

	@Test fun `current will not be saved when it is the same as the latest status`() {
		val current = SuccessfulStatusItem()
		val latest = SuccessfulStatusItem().copy(content = current.content)
		every { mockHistory.getCurrent(any()) }.returns(current)
		every { mockHistory.getLatest(any()) }.returns(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.NoChange(current, latest), result)

		verify { mockHistory.getCurrent(feed) }.wasInvoked()
		verify { mockHistory.getLatest(feed) }.wasInvoked()
		verify { mockHistory.save(any()) }.wasNotInvoked()
	}

	@Test fun `current will be saved when it differs from the latest status`() {
		val current = SuccessfulStatusItem()
		val latest = SuccessfulStatusItem()
		every { mockHistory.getCurrent(any()) }.returns(current)
		every { mockHistory.getLatest(any()) }.returns(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify { mockHistory.getCurrent(feed) }.wasInvoked()
		verify { mockHistory.getLatest(feed) }.wasInvoked()
		verify { mockHistory.save(current) }.wasInvoked()
	}

	@Test fun `current error will be saved when it differs from the latest status`() {
		val current = FailedStatusItem()
		val latest = SuccessfulStatusItem()
		every { mockHistory.getCurrent(any()) }.returns(current)
		every { mockHistory.getLatest(any()) }.returns(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify { mockHistory.getCurrent(feed) }.wasInvoked()
		verify { mockHistory.getLatest(feed) }.wasInvoked()
		verify { mockHistory.save(current) }.wasInvoked()
	}

	@Test fun `current success will be saved when it differs from the latest error`() {
		val current = SuccessfulStatusItem()
		val latest = FailedStatusItem()
		every { mockHistory.getCurrent(any()) }.returns(current)
		every { mockHistory.getLatest(any()) }.returns(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify { mockHistory.getCurrent(feed) }.wasInvoked()
		verify { mockHistory.getLatest(feed) }.wasInvoked()
		verify { mockHistory.save(current) }.wasInvoked()
	}

	@Test fun `current error will be saved when it differs from the latest error`() {
		val current = FailedStatusItem()
		val latest = FailedStatusItem()
		every { mockHistory.getCurrent(any()) }.returns(current)
		every { mockHistory.getLatest(any()) }.returns(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify { mockHistory.getCurrent(feed) }.wasInvoked()
		verify { mockHistory.getLatest(feed) }.wasInvoked()
		verify { mockHistory.save(current) }.wasInvoked()
	}

	@Test fun `current error will not be saved when it is the same as the latest error`() {
		val current = FailedStatusItem()
		val latest = FailedStatusItem().copy(error = current.error)
		every { mockHistory.getCurrent(any()) }.returns(current)
		every { mockHistory.getLatest(any()) }.returns(latest)

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.NoChange(current, latest), result)

		verify { mockHistory.getCurrent(feed) }.wasInvoked()
		verify { mockHistory.getLatest(feed) }.wasInvoked()
		verify { mockHistory.save(current) }.wasNotInvoked()
	}
}
