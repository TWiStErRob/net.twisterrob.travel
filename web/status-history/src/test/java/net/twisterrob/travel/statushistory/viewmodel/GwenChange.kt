package net.twisterrob.travel.statushistory.viewmodel

import com.shazam.gwen.collaborators.Actor
import com.shazam.gwen.collaborators.Asserter
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.StatusChange
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.emptyOrNullString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals

internal class GwenChange : Actor, Asserter {

	private lateinit var change: ResultChange

	fun between(status1: GwenStatus, status2: GwenStatus) {
		change = ResultChange(status1.createResult(), status2.createResult())
	}

	fun has(line: Line, status: StatusChange): GwenChange = apply {
		assertThat(change.statuses, hasEntry(line, status))
	}

	fun hasNoDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(change.descriptions, not(hasKey(line)))
		}
	}

	fun hasDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(change.descriptions, hasEntry(equalTo(line), not(emptyOrNullString())))
		}
	}

	fun hasNoErrorChange(): GwenChange = apply {
		assertEquals(ErrorChange.NoErrors, change.error)
	}
}
