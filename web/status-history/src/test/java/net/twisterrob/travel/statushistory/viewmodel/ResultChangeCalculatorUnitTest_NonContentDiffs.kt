package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import net.twisterrob.travel.statushistory.viewmodel.Changes.ErrorChanges
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.Date

class ResultChangeCalculatorUnitTest_NonContentDiffs {

	private val subject = ResultChangesCalculator()

	@Test fun testErrorChange() {
		val result1 = ErrorResult("error1")
		val result2 = ErrorResult("error2")

		val difference = subject.diff(result1, result2)

		assertEquals(difference, ErrorChanges.Change(result1, result2))
	}

	@Test fun testErrorNoChange() {
		val result1 = ErrorResult("error")
		val result2 = ErrorResult("error")

		val difference = subject.diff(result1, result2)

		assertEquals(difference, ErrorChanges.Same(result2))
	}

	@Test fun testErrorIntroduced() {
		val result1 = ContentResult(mock())
		val result2 = ErrorResult("error")

		val difference = subject.diff(result1, result2)

		assertEquals(difference, ErrorChanges.Failed(result1, result2))
	}

	@Test fun testErrorDisappeared() {
		val result1 = ErrorResult("error")
		val result2 = ContentResult(mock())

		val difference = subject.diff(result1, result2)

		assertEquals(difference, ErrorChanges.Fixed(result1, result2))
	}

	@Test fun testEmpty() {
		val result1 = ContentResult(mock())
		val result2 = ContentResult(mock())

		val difference = subject.diff(result1, result2)

		assertEquals(difference, Changes.Status(result1, result2, emptyMap()))
	}

	@Test fun testFirstOne() {
		val result: Result = ContentResult(mock())

		val difference = subject.diff(null, result)

		assertEquals(difference, Changes.NewStatus(result))
	}

	@Test fun testFirstError() {
		val result: Result = ErrorResult("error")

		val difference = subject.diff(null, result)

		assertEquals(difference, Changes.NewStatus(result))
	}

	@Test fun testLastOne() {
		val result: Result = ContentResult(mock())

		val difference = subject.diff(result, null)

		assertEquals(difference, Changes.LastStatus(result))
	}

	@Test fun testLastError() {
		val result: Result = ErrorResult("error")

		val difference = subject.diff(result, null)

		assertEquals(difference, Changes.LastStatus(result))
	}

	@Test fun testMissingResults() {
		val difference = subject.diff(null, null)

		assertEquals(difference, Changes.None)
	}
}

@Suppress("TestFunctionName")
private fun ContentResult(content: LineStatusFeed): Result.ContentResult =
	Result.ContentResult(Date(), content)

@Suppress("TestFunctionName")
private fun ErrorResult(error: String): Result.ErrorResult =
	Result.ErrorResult(Date(), Result.ErrorResult.Error(error))
