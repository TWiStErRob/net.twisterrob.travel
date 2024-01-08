package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.anEmptyMap
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.Date

class ResultChangeUnitTest_Errors {

	@Test fun testErrorChange() {
		val result1 = Result.ErrorResult(Date(), "error1")
		val result2 = Result.ErrorResult(Date(), "error2")

		val change = ResultChangeCalculator(result1, result2).diff()

		assertErrorAndNoChanges(change, ErrorChange.Change)
	}

	@Test fun testErrorNoChange() {
		val result1 = Result.ErrorResult(Date(), "error")
		val result2 = Result.ErrorResult(Date(), "error")

		val change = ResultChangeCalculator(result1, result2).diff()

		assertErrorAndNoChanges(change, ErrorChange.Same)
	}

	@Test fun testErrorIntroduced() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ErrorResult(Date(), "error")

		val change = ResultChangeCalculator(result1, result2).diff()

		assertErrorAndNoChanges(change, ErrorChange.Failed)
	}

	@Test fun testErrorDisappeared() {
		val result1 = Result.ErrorResult(Date(), "error")
		val result2 = Result.ContentResult(Date(), mock())

		val change = ResultChangeCalculator(result1, result2).diff()

		assertErrorAndNoChanges(change, ErrorChange.Fixed)
	}

	@Test fun testErrorNone() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = ResultChangeCalculator(result1, result2).diff()

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testFirstOne() {
		val result: Result = Result.ContentResult(Date(), mock())

		val change = ResultChangeCalculator(null, result).diff()

		assertErrorAndNoChanges(change, ErrorChange.NewStatus)
	}

	@Test fun testFirstError() {
		val result: Result = Result.ErrorResult(Date(), "error")

		val change = ResultChangeCalculator(null, result).diff()

		assertErrorAndNoChanges(change, ErrorChange.NewStatus)
	}

	@Test fun testLastOne() {
		val result: Result = Result.ContentResult(Date(), mock())

		val change = ResultChangeCalculator(result, null).diff()

		assertErrorAndNoChanges(change, ErrorChange.LastStatus)
	}

	@Test fun testLastError() {
		val result: Result = Result.ErrorResult(Date(), "error")

		val change = ResultChangeCalculator(result, null).diff()

		assertErrorAndNoChanges(change, ErrorChange.LastStatus)
	}

	@Test fun testMissingResults() {
		val change = ResultChangeCalculator(null, null).diff()

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testErrorNewFeedMissing() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = ResultChangeCalculator(result1, result2).diff()

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testErrorOldFeedMissing() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = ResultChangeCalculator(result2, result1).diff()

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
