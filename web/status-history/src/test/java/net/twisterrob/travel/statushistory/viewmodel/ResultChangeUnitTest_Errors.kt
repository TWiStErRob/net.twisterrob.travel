package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.anEmptyMap
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.Date

class ResultChangeUnitTest_Errors {

	@Test fun testErrorChange() {
		val result1 = Result(Date(), "error1")
		val result2 = Result(Date(), "error2")

		val change = ResultChange(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.Change)
	}

	@Test fun testErrorNoChange() {
		val result1 = Result(Date(), "error")
		val result2 = Result(Date(), "error")

		val change = ResultChange(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.Same)
	}

	@Test fun testErrorIntroduced() {
		val result1 = Result(Date(), null as LineStatusFeed?)
		val result2 = Result(Date(), "error")

		val change = ResultChange(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.Failed)
	}

	@Test fun testErrorDisappeared() {
		val result1 = Result(Date(), "error")
		val result2 = Result(Date(), null as LineStatusFeed?)

		val change = ResultChange(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.Fixed)
	}

	@Test fun testErrorNone() {
		val result1 = Result(Date(), null as LineStatusFeed?)
		val result2 = Result(Date(), null as LineStatusFeed?)

		val change = ResultChange(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testFirstOne() {
		val result: Result = mock()

		val change = ResultChange(null, result)

		assertErrorAndNoChanges(change, ErrorChange.NewStatus)
	}

	@Test fun testLastOne() {
		val result: Result = mock()

		val change = ResultChange(result, null)

		assertErrorAndNoChanges(change, ErrorChange.LastStatus)
	}

	@Test fun testMissingResults() {
		val change = ResultChange(null, null)

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testErrorNewFeedMissing() {
		val result1 = Result(Date(), mock<LineStatusFeed>())
		val result2 = Result(Date(), null as LineStatusFeed?)

		val change = ResultChange(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testErrorOldFeedMissing() {
		val result1 = Result(Date(), mock<LineStatusFeed>())
		val result2 = Result(Date(), null as LineStatusFeed?)

		val change = ResultChange(result2, result1)

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	companion object {

		private fun assertErrorAndNoChanges(result: ResultChange, errors: ErrorChange) {
			assertEquals(errors, result.error)
			assertThat(result.statuses, `is`(anEmptyMap()))
			assertThat(result.descriptions, `is`(anEmptyMap()))
		}
	}
}
