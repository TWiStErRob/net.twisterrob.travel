package net.twisterrob.travel.domain.london.status

import net.twisterrob.blt.model.LineStatuses
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.IOException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @see StatusHistoryRepository.history
 */
class DomainHistoryRepositoryUnitTest_history {

	private val mockHistory: StatusHistoryDataSource = mock()
	private val mockStatus: StatusDataSource = mock()
	private val mockParser: FeedParser = mock()
	private val subject: StatusHistoryRepository = DomainStatusHistoryRepository(mockHistory, mockStatus, mockParser)
	private val feed = Feed.TubeDepartureBoardsLineStatus

	@AfterTest
	fun verify() {
		verifyNoMoreInteractions(mockHistory, mockStatus, mockParser)
	}

	@Test fun `empty history is processed`() {
		whenever(mockHistory.getAll(any(), any())).thenReturn(emptyList())

		val result = subject.history(feed, max = 123, includeCurrent = false)
		assertEquals(emptyList(), result)

		verify(mockHistory).getAll(feed, 123)
	}

	@Test fun `empty history with current is returned`() {
		val current = SuccessfulStatusItem()
		whenever(mockHistory.getAll(any(), any())).thenReturn(emptyList())
		whenever(mockStatus.getCurrent(any())).thenReturn(current)
		val parsed = LineStatuses()
		whenever(mockParser.parse(any(), any())).thenReturn(parsed)

		val result = subject.history(feed, max = 123, includeCurrent = true)
		val expected = listOf(
			current.toParsed(parsed)
		)
		assertEquals(expected, result)

		verify(mockHistory).getAll(feed, 123)
		verify(mockStatus).getCurrent(feed)
		verify(mockParser).parse(current.feed, current.content)
	}

	@Test fun `some history with current is prepended`() {
		val current = SuccessfulStatusItem()
		val existing1 = SuccessfulStatusItem()
		val existing2 = SuccessfulStatusItem()
		whenever(mockHistory.getAll(any(), any())).thenReturn(listOf(existing1, existing2))
		whenever(mockStatus.getCurrent(any())).thenReturn(current)
		val currentParsed = LineStatuses()
		val existingParsed1 = LineStatuses()
		val existingParsed2 = LineStatuses()
		whenever(mockParser.parse(any(), any())).thenReturn(currentParsed, existingParsed1, existingParsed2)

		val result = subject.history(feed, max = 123, includeCurrent = true)
		val expected = listOf(
			current.toParsed(currentParsed),
			existing1.toParsed(existingParsed1),
			existing2.toParsed(existingParsed2),
		)
		assertEquals(expected, result)

		verify(mockHistory).getAll(feed, 123)
		verify(mockStatus).getCurrent(feed)
		verify(mockParser).parse(current.feed, current.content)
		verify(mockParser).parse(existing1.feed, existing1.content)
		verify(mockParser).parse(existing2.feed, existing2.content)
	}

	@Test fun `failed existing is returned`() {
		val existing1 = FailedStatusItem()
		val existing2 = SuccessfulStatusItem()
		whenever(mockHistory.getAll(any(), any())).thenReturn(listOf(existing1, existing2))
		val existingParsed2 = LineStatuses()
		whenever(mockParser.parse(any(), any())).thenReturn(existingParsed2)

		val result = subject.history(feed, max = 123, includeCurrent = false)
		val expected = listOf(
			existing1.toParsed(),
			existing2.toParsed(existingParsed2),
		)
		assertEquals(expected, result)

		verify(mockHistory).getAll(feed, 123)
		verify(mockParser).parse(existing2.feed, existing2.content)
	}

	@Test fun `failed current is returned`() {
		val current = FailedStatusItem()
		val existing1 = SuccessfulStatusItem()
		val existing2 = SuccessfulStatusItem()
		whenever(mockHistory.getAll(any(), any())).thenReturn(listOf(existing1, existing2))
		whenever(mockStatus.getCurrent(any())).thenReturn(current)
		val existingParsed1 = LineStatuses()
		val existingParsed2 = LineStatuses()
		whenever(mockParser.parse(any(), any())).thenReturn(existingParsed1, existingParsed2)

		val result = subject.history(feed, max = 123, includeCurrent = true)
		val expected = listOf(
			current.toParsed(),
			existing1.toParsed(existingParsed1),
			existing2.toParsed(existingParsed2),
		)
		assertEquals(expected, result)

		verify(mockHistory).getAll(feed, 123)
		verify(mockStatus).getCurrent(feed)
		verify(mockParser).parse(existing1.feed, existing1.content)
		verify(mockParser).parse(existing2.feed, existing2.content)
	}

	@Test fun `existing failing to parse is returned`() {
		val existing1 = SuccessfulStatusItem()
		val existing2 = SuccessfulStatusItem()
		whenever(mockHistory.getAll(any(), any())).thenReturn(listOf(existing1, existing2))
		val existingParsed1 = LineStatuses()
		val ex = IOException("Failed to parse XML")
		whenever(mockParser.parse(any(), any()))
			.thenAnswer { existingParsed1 }
			.thenAnswer { throw ex }

		val result = subject.history(feed, max = 123, includeCurrent = false)
		val expected = listOf(
			existing1.toParsed(existingParsed1),
			existing2.toParsed(ex),
		)
		assertEquals(expected, result)

		verify(mockHistory).getAll(feed, 123)
		verify(mockParser).parse(existing1.feed, existing1.content)
		verify(mockParser).parse(existing2.feed, existing2.content)
	}

	@Test fun `current failing to parse is returned`() {
		val current = SuccessfulStatusItem()
		val existing1 = SuccessfulStatusItem()
		val existing2 = SuccessfulStatusItem()
		whenever(mockHistory.getAll(any(), any())).thenReturn(listOf(existing1, existing2))
		whenever(mockStatus.getCurrent(any())).thenReturn(current)
		val existingParsed1 = LineStatuses()
		val existingParsed2 = LineStatuses()
		val ex = IOException("Failed to parse XML")
		whenever(mockParser.parse(any(), any()))
			.thenAnswer { throw ex }
			.thenAnswer { existingParsed1 }
			.thenAnswer { existingParsed2 }

		val result = subject.history(feed, max = 123, includeCurrent = true)
		val expected = listOf(
			current.toParsed(ex),
			existing1.toParsed(existingParsed1),
			existing2.toParsed(existingParsed2),
		)
		assertEquals(expected, result)

		verify(mockHistory).getAll(feed, 123)
		verify(mockStatus).getCurrent(feed)
		verify(mockParser).parse(current.feed, current.content)
		verify(mockParser).parse(existing1.feed, existing1.content)
		verify(mockParser).parse(existing2.feed, existing2.content)
	}
}

private fun StatusItem.SuccessfulStatusItem.toParsed(parsed: LineStatuses): ParsedStatusItem =
	ParsedStatusItem.ParsedFeed(this, parsed)

private fun StatusItem.FailedStatusItem.toParsed(): ParsedStatusItem =
	ParsedStatusItem.AlreadyFailed(this)

private fun StatusItem.SuccessfulStatusItem.toParsed(ex: Throwable): ParsedStatusItem =
	ParsedStatusItem.ParseFailed(this, Stacktrace(ex.stackTraceToString()))

@Suppress("TestFunctionName")
private fun LineStatuses(): LineStatuses =
	LineStatuses(emptyList())
