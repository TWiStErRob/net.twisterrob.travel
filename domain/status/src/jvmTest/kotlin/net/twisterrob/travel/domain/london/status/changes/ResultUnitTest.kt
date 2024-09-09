package net.twisterrob.travel.domain.london.status.changes

import net.twisterrob.blt.model.LineStatuses
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @see Result
 */
class ResultUnitTest {

	@Test fun testConsistentPropertiesError() {
		val error = Result.ErrorResult.Error("error")
		val date = Date()

		val result = Result.ErrorResult(date, error)

		assertEquals(error, result.error)
		assertEquals(date, result.`when`)
	}

	@Test fun testConsistentPropertiesFeed() {
		val feed = LineStatuses(emptyList())
		val date = Date()

		val result = Result.ContentResult(date, feed)

		assertEquals(feed, result.content)
		assertEquals(date, result.`when`)
	}
}
