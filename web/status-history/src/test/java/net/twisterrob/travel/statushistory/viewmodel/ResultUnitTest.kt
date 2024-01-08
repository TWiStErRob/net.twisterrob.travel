package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.Date

class ResultUnitTest {

	@Test fun testSingleLineErrorHeader() {
		val error = "Error message"
		val errorHeader = "Error message"

		val result = Result.ErrorResult(Date(), error)

		assertEquals(errorHeader, result.errorHeader)
		assertEquals(error, result.fullError)
	}

	@Test fun testOneLineErrorHeader() {
		val error = "Error message\n"
		val errorHeader = "Error message"

		val result = Result.ErrorResult(Date(), error)

		assertEquals(errorHeader, result.errorHeader)
		assertEquals(error, result.fullError)
	}

	@Test fun testMultiLineErrorHeader() {
		val error = "Error message\nSecond line\nThird line"
		val errorHeader = "Error message"

		val result = Result.ErrorResult(Date(), error)

		assertEquals(errorHeader, result.errorHeader)
		assertEquals(error, result.fullError)
	}

	@Test fun testConsistentPropertiesError() {
		val error = "error"
		val date: Date = mock()

		val result = Result.ErrorResult(date, error)

		assertEquals(error, result.fullError)
		assertEquals(date, result.`when`)
	}

	@Test fun testConsistentPropertiesFeed() {
		val feed: LineStatusFeed = mock()
		val date: Date = mock()

		val result = Result.ContentResult(date, feed)

		assertEquals(feed, result.content)
		assertEquals(date, result.`when`)
	}
}
