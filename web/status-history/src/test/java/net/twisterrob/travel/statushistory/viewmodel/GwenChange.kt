package net.twisterrob.travel.statushistory.viewmodel

import com.shazam.gwen.collaborators.Actor
import com.shazam.gwen.collaborators.Asserter
import net.twisterrob.blt.io.feeds.trackernet.model.DelayType
import net.twisterrob.blt.model.Line
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.HasDescriptionChange
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.either
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not

internal class GwenChange : Actor, Asserter {

	private lateinit var change: ResultChange

	private val changes: Map<Line, ResultChange.StatusChange>
		get() = (change.error as ResultChange.ErrorChange.NoErrors).changes

	fun between(status1: GwenStatus, status2: GwenStatus) {
		change = ResultChangeCalculator().diff(status1.createResult(), status2.createResult())
	}

	fun has(line: Line, desc: ResultChange.DescriptionChange): GwenChange = apply {
		assertThat(changes, hasEntry(line, ResultChange.StatusChange.Same(DelayType.Unknown, desc)))
	}

	fun has(line: Line, status: ResultChange.StatusChange): GwenChange = apply {
		assertThat(changes, hasEntry(line, status))
	}

	fun hasNoDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(changes, either(not(hasKey(line))).or(not(instanceOf(HasDescriptionChange::class.java))))
		}
	}

	fun hasDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(changes, hasEntry(equalTo(line), instanceOf(HasDescriptionChange::class.java)))
		}
	}

	fun hasNoErrorChange(): GwenChange = apply {
		assertThat(change.error, instanceOf(ResultChange.ErrorChange.NoErrors::class.java))
	}
}
