package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.travel.statushistory.viewmodel.ResultChangeModel.ErrorChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChangeModel.StatusChange
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

class ResultChangeModelUnitTest {

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
