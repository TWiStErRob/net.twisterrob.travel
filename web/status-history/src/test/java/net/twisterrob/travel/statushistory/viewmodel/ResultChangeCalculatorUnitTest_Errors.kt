package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.Date

class ResultChangeCalculatorUnitTest_Errors {

	private val subject = ResultChangeCalculator()

	@Test fun testErrorChange() {
		val error1 = Result.ErrorResult.Error("error1")
		val result1 = Result.ErrorResult(Date(), error1)
		val error2 = Result.ErrorResult.Error("error2")
		val result2 = Result.ErrorResult(Date(), error2)

		val change = subject.diff(result1, result2)

		assertError(change, ErrorChange.Change(error1, error2))
	}

	@Test fun testErrorNoChange() {
		val result1 = Result.ErrorResult(Date(), Result.ErrorResult.Error("error"))
		val result2 = Result.ErrorResult(Date(), Result.ErrorResult.Error("error"))

		val change = subject.diff(result1, result2)

		assertError(change, ErrorChange.Same(Result.ErrorResult.Error("error")))
	}

	@Test fun testErrorIntroduced() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ErrorResult(Date(), Result.ErrorResult.Error("error"))

		val change = subject.diff(result1, result2)

		assertError(change, ErrorChange.Failed(Result.ErrorResult.Error("error")))
	}

	@Test fun testErrorDisappeared() {
		val result1 = Result.ErrorResult(Date(), Result.ErrorResult.Error("error"))
		val result2 = Result.ContentResult(Date(), mock())

		val change = subject.diff(result1, result2)

		assertError(change, ErrorChange.Fixed(Result.ErrorResult.Error("error")))
	}

	@Test fun testErrorNone() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = subject.diff(result1, result2)

		assertError(change, ErrorChange.NoErrors(emptyMap()))
	}

	@Test fun testFirstOne() {
		val result: Result = Result.ContentResult(Date(), mock())

		val change = subject.diff(null, result)

		assertError(change, ErrorChange.NewStatus)
	}

	@Test fun testFirstError() {
		val result: Result = Result.ErrorResult(Date(), Result.ErrorResult.Error("error"))

		val change = subject.diff(null, result)

		assertError(change, ErrorChange.NewStatus)
	}

	@Test fun testLastOne() {
		val result: Result = Result.ContentResult(Date(), mock())

		val change = subject.diff(result, null)

		assertError(change, ErrorChange.LastStatus)
	}

	@Test fun testLastError() {
		val result: Result = Result.ErrorResult(Date(), Result.ErrorResult.Error("error"))

		val change = subject.diff(result, null)

		assertError(change, ErrorChange.LastStatus)
	}

	@Test fun testMissingResults() {
		val change = subject.diff(null, null)

		assertError(change, ErrorChange.NoErrors(emptyMap()))
	}

	@Test fun testErrorNewFeedMissing() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = subject.diff(result1, result2)

		assertError(change, ErrorChange.NoErrors(emptyMap()))
	}

	@Test fun testErrorOldFeedMissing() {
		val result1 = Result.ContentResult(Date(), mock())
		val result2 = Result.ContentResult(Date(), mock())

		val change = subject.diff(result2, result1)

		assertError(change, ErrorChange.NoErrors(emptyMap()))
	}

	companion object {

		private fun assertError(result: ResultChange, errors: ErrorChange) {
			assertEquals(errors, result.error)
		}
	}
}
