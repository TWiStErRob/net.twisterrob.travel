package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.Date

class ResultUnitTest {

	@Test fun testConsistentPropertiesError() {
		val error = Result.ErrorResult.Error("error")
		val date: Date = mock()

		val result = Result.ErrorResult(date, error)

		assertEquals(error, result.error)
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
