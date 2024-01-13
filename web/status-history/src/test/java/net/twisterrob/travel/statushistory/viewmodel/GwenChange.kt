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

	private lateinit var difference: Changes

	private val statusChanges: Map<Line, StatusChange>
		get() = (difference as Changes.Status).changes

	fun between(status1: GwenStatus, status2: GwenStatus) {
		difference = ResultChangesCalculator().diff(status1.createResult(), status2.createResult())
	}

	fun has(line: Line, desc: DescriptionChange): GwenChange = apply {
		assertThat(statusChanges, hasEntry(line, StatusChange.Same(DelayType.Unknown, desc)))
	}

	fun has(line: Line, status: StatusChange): GwenChange = apply {
		assertThat(statusChanges, hasEntry(line, status))
	}

	fun hasNoStatusChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(statusChanges, not(hasKey(line)))
		}
	}

	fun hasNoDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(statusChanges, either(not(hasKey(line))).or(not(instanceOf(HasDescriptionChange::class.java))))
		}
	}

	fun hasDescriptionChangeFor(vararg lines: Line): GwenChange = apply {
		for (line in lines) {
			assertThat(statusChanges, hasEntry(equalTo(line), instanceOf(HasDescriptionChange::class.java)))
		}
	}

	fun hasNoErrorChange(): GwenChange = apply {
		assertThat(difference, instanceOf(Changes.Status::class.java))
	}
}
