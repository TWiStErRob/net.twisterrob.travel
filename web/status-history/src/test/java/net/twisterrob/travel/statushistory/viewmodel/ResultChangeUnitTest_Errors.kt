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

	private val subject = ResultChangeCalculator()

	@Test fun testErrorChange() {
		val result1 = Result.ErrorResult(Date(), "error1")
		val result2 = Result.ErrorResult(Date(), "error2")

		val change = subject.diff(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.Change)
	}

	@Test fun testErrorNoChange() {
		val result1 = Result.ErrorResult(Date(), "error")
		val result2 = Result.ErrorResult(Date(), "error")

		val change = subject.diff(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.Same)
	}

	@Test fun testErrorIntroduced() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ErrorResult(Date(), "error")

		val change = subject.diff(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.Failed)
	}

	@Test fun testErrorDisappeared() {
		val result1 = Result.ErrorResult(Date(), "error")
		val result2 = Result.ContentResult(Date(), mock())

		val change = subject.diff(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.Fixed)
	}

	@Test fun testErrorNone() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = subject.diff(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testFirstOne() {
		val result: Result = Result.ContentResult(Date(), mock())

		val change = subject.diff(null, result)

		assertErrorAndNoChanges(change, ErrorChange.NewStatus)
	}

	@Test fun testFirstError() {
		val result: Result = Result.ErrorResult(Date(), "error")

		val change = subject.diff(null, result)

		assertErrorAndNoChanges(change, ErrorChange.NewStatus)
	}

	@Test fun testLastOne() {
		val result: Result = Result.ContentResult(Date(), mock())

		val change = subject.diff(result, null)

		assertErrorAndNoChanges(change, ErrorChange.LastStatus)
	}

	@Test fun testLastError() {
		val result: Result = Result.ErrorResult(Date(), "error")

		val change = subject.diff(result, null)

		assertErrorAndNoChanges(change, ErrorChange.LastStatus)
	}

	@Test fun testMissingResults() {
		val change = subject.diff(null, null)

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testErrorNewFeedMissing() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = subject.diff(result1, result2)

		assertErrorAndNoChanges(change, ErrorChange.NoErrors)
	}

	@Test fun testErrorOldFeedMissing() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = subject.diff(result2, result1)

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
