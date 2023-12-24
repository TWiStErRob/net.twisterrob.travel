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
import net.twisterrob.travel.domain.london.status.api.StatusInteractor
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainRefreshUseCaseUnitTest {

	private val mockRepo: StatusHistoryRepository = mock()
	private val mockInteractor: StatusInteractor = mock()
	private val subject: RefreshUseCase = DomainRefreshUseCase(mockRepo, mockInteractor)
	private val feed = Feed.TubeDepartureBoardsLineStatus

	@AfterTest
	fun verify() {
		listOf(mockRepo, mockInteractor).forEach {
			verifyNoUnverifiedExpectations(it)
			verifyNoUnmetExpectations(it)
		}
	}

	@Test fun `current will be saved when there are no previous statuses`() {
		val current = SuccessfulStatusItem()
		every { mockInteractor.getCurrent(any()) }.returns(current)
		every { mockRepo.getAll(any(), any()) }.returns(emptyList())

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Created(current), result)

		verify { mockInteractor.getCurrent(feed) }.wasInvoked()
		verify { mockRepo.getAll(feed, 1) }.wasInvoked()
		verify { mockRepo.add(current) }.wasInvoked()
	}

	@Test fun `current will not be saved when it is the same as the latest status`() {
		val current = SuccessfulStatusItem()
		val latest = SuccessfulStatusItem().copy(content = current.content)
		every { mockInteractor.getCurrent(any()) }.returns(current)
		every { mockRepo.getAll(any(), any()) }.returns(listOf(latest))

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.NoChange(current, latest), result)

		verify { mockInteractor.getCurrent(feed) }.wasInvoked()
		verify { mockRepo.getAll(feed, 1) }.wasInvoked()
		verify { mockRepo.add(any()) }.wasNotInvoked()
	}

	@Test fun `current will be saved when it differs from the latest status`() {
		val current = SuccessfulStatusItem()
		val latest = SuccessfulStatusItem()
		every { mockInteractor.getCurrent(any()) }.returns(current)
		every { mockRepo.getAll(any(), any()) }.returns(listOf(latest))

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify { mockInteractor.getCurrent(feed) }.wasInvoked()
		verify { mockRepo.getAll(feed, 1) }.wasInvoked()
		verify { mockRepo.add(current) }.wasInvoked()
	}

	@Test fun `current error will be saved when it differs from the latest status`() {
		val current = FailedStatusItem()
		val latest = SuccessfulStatusItem()
		every { mockInteractor.getCurrent(any()) }.returns(current)
		every { mockRepo.getAll(any(), any()) }.returns(listOf(latest))

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify { mockInteractor.getCurrent(feed) }.wasInvoked()
		verify { mockRepo.getAll(feed, 1) }.wasInvoked()
		verify { mockRepo.add(current) }.wasInvoked()
	}

	@Test fun `current success will be saved when it differs from the latest error`() {
		val current = SuccessfulStatusItem()
		val latest = FailedStatusItem()
		every { mockInteractor.getCurrent(any()) }.returns(current)
		every { mockRepo.getAll(any(), any()) }.returns(listOf(latest))

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify { mockInteractor.getCurrent(feed) }.wasInvoked()
		verify { mockRepo.getAll(feed, 1) }.wasInvoked()
		verify { mockRepo.add(current) }.wasInvoked()
	}

	@Test fun `current error will be saved when it differs from the latest error`() {
		val current = FailedStatusItem()
		val latest = FailedStatusItem()
		every { mockInteractor.getCurrent(any()) }.returns(current)
		every { mockRepo.getAll(any(), any()) }.returns(listOf(latest))

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.Refreshed(current, latest), result)

		verify { mockInteractor.getCurrent(feed) }.wasInvoked()
		verify { mockRepo.getAll(feed, 1) }.wasInvoked()
		verify { mockRepo.add(current) }.wasInvoked()
	}

	@Test fun `current error will not be saved when it is the same as the latest error`() {
		val current = FailedStatusItem()
		val latest = FailedStatusItem().copy(error = current.error)
		every { mockInteractor.getCurrent(any()) }.returns(current)
		every { mockRepo.getAll(any(), any()) }.returns(listOf(latest))

		val result = subject.refreshLatest(feed)
		assertEquals(RefreshResult.NoChange(current, latest), result)

		verify { mockInteractor.getCurrent(feed) }.wasInvoked()
		verify { mockRepo.getAll(feed, 1) }.wasInvoked()
		verify { mockRepo.add(current) }.wasNotInvoked()
	}
}
