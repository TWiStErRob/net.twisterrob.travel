package net.twisterrob.travel.domain.london.status

import io.mockative.any
import io.mockative.every
import io.mockative.mock
import io.mockative.verify
import io.mockative.verifyNoUnmetExpectations
import io.mockative.verifyNoUnverifiedExpectations
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import java.io.IOException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DomainHistoryRepositoryUnitTest_history {

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

	@Test fun `empty history is processed`() {
		every { mockHistory.getAll(any(), any()) }.returns(emptyList())

		val result = subject.history(feed, max = 123, includeCurrent = false)
		assertEquals(emptyList(), result)

		verify { mockHistory.getAll(feed, 123) }.wasInvoked()
	}

	@Test fun `empty history with current is returned`() {
		val current = SuccessfulStatusItem()
		every { mockHistory.getAll(any(), any()) }.returns(emptyList())
		every { mockStatus.getCurrent(any()) }.returns(current)
		val parsed = Any()
		every { mockParser.parse(any(), any()) }.returns(parsed)

		val result = subject.history(feed, max = 123, includeCurrent = true)
		val expected = listOf(
			current.toParsed(parsed)
		)
		assertEquals(expected, result)

		verify { mockHistory.getAll(feed, 123) }.wasInvoked()
		verify { mockStatus.getCurrent(feed) }.wasInvoked()
		verify { mockParser.parse(current.feed, current.content) }.wasInvoked()
	}

	@Test fun `some history with current is prepended`() {
		val current = SuccessfulStatusItem()
		val existing1 = SuccessfulStatusItem()
		val existing2 = SuccessfulStatusItem()
		every { mockHistory.getAll(any(), any()) }.returns(listOf(existing1, existing2))
		every { mockStatus.getCurrent(any()) }.returns(current)
		val currentParsed = Any()
		val existingParsed1 = Any()
		val existingParsed2 = Any()
		every { mockParser.parse(any(), any()) }.returnsMany(currentParsed, existingParsed1, existingParsed2)

		val result = subject.history(feed, max = 123, includeCurrent = true)
		val expected = listOf(
			current.toParsed(currentParsed),
			existing1.toParsed(existingParsed1),
			existing2.toParsed(existingParsed2),
		)
		assertEquals(expected, result)

		verify { mockHistory.getAll(feed, 123) }.wasInvoked()
		verify { mockStatus.getCurrent(feed) }.wasInvoked()
		verify { mockParser.parse(current.feed, current.content) }.wasInvoked()
		verify { mockParser.parse(existing1.feed, existing1.content) }.wasInvoked()
		verify { mockParser.parse(existing2.feed, existing2.content) }.wasInvoked()
	}

	@Test fun `failed existing is returned`() {
		val existing1 = FailedStatusItem()
		val existing2 = SuccessfulStatusItem()
		every { mockHistory.getAll(any(), any()) }.returns(listOf(existing1, existing2))
		val existingParsed2 = Any()
		every { mockParser.parse(any(), any()) }.returns(existingParsed2)

		val result = subject.history(feed, max = 123, includeCurrent = false)
		val expected = listOf(
			existing1.toParsed(),
			existing2.toParsed(existingParsed2),
		)
		assertEquals(expected, result)

		verify { mockHistory.getAll(feed, 123) }.wasInvoked()
		verify { mockParser.parse(existing2.feed, existing2.content) }.wasInvoked()
	}

	@Test fun `failed current is returned`() {
		val current = FailedStatusItem()
		val existing1 = SuccessfulStatusItem()
		val existing2 = SuccessfulStatusItem()
		every { mockHistory.getAll(any(), any()) }.returns(listOf(existing1, existing2))
		every { mockStatus.getCurrent(any()) }.returns(current)
		val existingParsed1 = Any()
		val existingParsed2 = Any()
		every { mockParser.parse(any(), any()) }.returnsMany(existingParsed1, existingParsed2)

		val result = subject.history(feed, max = 123, includeCurrent = true)
		val expected = listOf(
			current.toParsed(),
			existing1.toParsed(existingParsed1),
			existing2.toParsed(existingParsed2),
		)
		assertEquals(expected, result)

		verify { mockHistory.getAll(feed, 123) }.wasInvoked()
		verify { mockStatus.getCurrent(feed) }.wasInvoked()
		verify { mockParser.parse(existing1.feed, existing1.content) }.wasInvoked()
		verify { mockParser.parse(existing2.feed, existing2.content) }.wasInvoked()
	}

	@Test fun `existing failing to parse is returned`() {
		val existing1 = SuccessfulStatusItem()
		val existing2 = SuccessfulStatusItem()
		every { mockHistory.getAll(any(), any()) }.returns(listOf(existing1, existing2))
		val existingParsed1 = Any()
		val ex = IOException("Failed to parse XML")
		every { mockParser.parse(any(), any()) }.invokesMany({ existingParsed1 }, { throw ex })

		val result = subject.history(feed, max = 123, includeCurrent = false)
		val expected = listOf(
			existing1.toParsed(existingParsed1),
			existing2.toParsed(ex),
		)
		assertEquals(expected, result)

		verify { mockHistory.getAll(feed, 123) }.wasInvoked()
		verify { mockParser.parse(existing1.feed, existing1.content) }.wasInvoked()
		verify { mockParser.parse(existing2.feed, existing2.content) }.wasInvoked()
	}

	@Test fun `current failing to parse is returned`() {
		val current = SuccessfulStatusItem()
		val existing1 = SuccessfulStatusItem()
		val existing2 = SuccessfulStatusItem()
		every { mockHistory.getAll(any(), any()) }.returns(listOf(existing1, existing2))
		every { mockStatus.getCurrent(any()) }.returns(current)
		val existingParsed1 = Any()
		val existingParsed2 = Any()
		val ex = IOException("Failed to parse XML")
		every { mockParser.parse(any(), any()) }.invokesMany({ throw ex }, { existingParsed1 }, { existingParsed2 })

		val result = subject.history(feed, max = 123, includeCurrent = true)
		val expected = listOf(
			current.toParsed(ex),
			existing1.toParsed(existingParsed1),
			existing2.toParsed(existingParsed2),
		)
		assertEquals(expected, result)

		verify { mockHistory.getAll(feed, 123) }.wasInvoked()
		verify { mockStatus.getCurrent(feed) }.wasInvoked()
		verify { mockParser.parse(current.feed, current.content) }.wasInvoked()
		verify { mockParser.parse(existing1.feed, existing1.content) }.wasInvoked()
		verify { mockParser.parse(existing2.feed, existing2.content) }.wasInvoked()
	}
}

private fun StatusItem.SuccessfulStatusItem.toParsed(parsed: Any): ParsedStatusItem =
	ParsedStatusItem.ParsedFeed(this, parsed)

private fun StatusItem.FailedStatusItem.toParsed(): ParsedStatusItem =
	ParsedStatusItem.AlreadyFailed(this)

private fun StatusItem.SuccessfulStatusItem.toParsed(ex: Throwable): ParsedStatusItem =
	ParsedStatusItem.ParseFailed(this, Stacktrace(ex.stackTraceToString()))
