package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.StatusChange
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.util.Date

class ResultChangeUnitTest {

	@Test fun testConsistentProperties() {
		val result1: Result = Result.ContentResult(Date(), mock())
		val result2: Result = Result.ErrorResult(Date(), "error")

		val change = ResultChange(result1, result2, ErrorChange.NoErrors, emptyMap(), emptyMap())

		assertSame(result1, change.previous)
		assertSame(result2, change.current)
	}

	@Test fun testStatusChange() {
		for (change in StatusChange.entries) {
			assertThat(change.name, change.title, `is`(notNullValue()))
			assertThat(change.name, change.cssClass, not(`is`(emptyOrNullString())))
		}
	}

	@Test fun testErrorChange() {
		for (change in ErrorChange.entries) {
			assertThat(change.name, change.title, `is`(notNullValue()))
		}
	}
}
