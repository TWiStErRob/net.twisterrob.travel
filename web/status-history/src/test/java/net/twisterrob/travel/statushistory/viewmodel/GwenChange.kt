package net.twisterrob.travel.statushistory.viewmodel

import com.shazam.gwen.collaborators.Actor
import com.shazam.gwen.collaborators.Asserter
import net.twisterrob.blt.io.feeds.trackernet.model.DelayType
import net.twisterrob.blt.model.Line
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.either
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.Matchers.not

internal class GwenChange : Actor, Asserter {

	private lateinit var changes: Changes

	fun between(status1: GwenStatus, status2: GwenStatus) {
		changes = ResultChangesCalculator().diff(status1.createResult(), status2.createResult())
	}

	fun has(line: Line, desc: DescriptionChange): GwenChange = apply {
		assertThat(changes.status, hasEntry(line, StatusChange.Same(DelayType.Unknown, desc)))
	}

	fun has(line: Line, status: StatusChange): GwenChange = apply {
		assertThat(changes.status, hasEntry(line, status))
	}

	fun hasNoStatusChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(changes.status, not(hasKey(line)))
		}
	}

	fun hasNoDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(changes.status, either(not(hasKey(line))).or(not(instanceOf(HasDescriptionChange::class.java))))
		}
	}

	fun hasDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(changes.status, hasEntry(equalTo(line), instanceOf(HasDescriptionChange::class.java)))
		}
	}

	fun hasNoErrorChange(): GwenChange = apply {
		assertThat(changes, instanceOf(Changes.Status::class.java))
	}
}

private val Changes.status: Map<Line, StatusChange>
	get() = (this as Changes.Status).changes
