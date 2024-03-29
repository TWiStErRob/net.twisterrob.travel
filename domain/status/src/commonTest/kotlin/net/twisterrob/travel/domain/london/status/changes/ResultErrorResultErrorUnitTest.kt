package net.twisterrob.travel.domain.london.status.changes

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @see Result.ErrorResult.Error
 */
class ResultErrorResultErrorUnitTest {

	@Test fun testSingleLineErrorHeader() {
		val error = "Error message"
		val errorHeader = "Error message"

		val result = Result.ErrorResult.Error(error)

		assertEquals(error, result.text)
		assertEquals(errorHeader, result.header)
	}

	@Test fun testOneLineErrorHeader() {
		val error = "Error message\n"
		val errorHeader = "Error message"

		val result = Result.ErrorResult.Error(error)

		assertEquals(error, result.text)
		assertEquals(errorHeader, result.header)
	}

	@Test fun testMultiLineErrorHeader() {
		val error = "Error message\nSecond line\nThird line"
		val errorHeader = "Error message"

		val result = Result.ErrorResult.Error(error)

		assertEquals(error, result.text)
		assertEquals(errorHeader, result.header)
	}
}
