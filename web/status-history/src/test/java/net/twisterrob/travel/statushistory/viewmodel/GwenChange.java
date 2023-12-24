package net.twisterrob.travel.statushistory.viewmodel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import com.shazam.gwen.collaborators.*;

import net.twisterrob.blt.model.Line;
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.ErrorChange;
import net.twisterrob.travel.statushistory.viewmodel.ResultChange.StatusChange;

class GwenChange implements Actor, Asserter {
	private ResultChange change;

	public void between(GwenStatus status1, GwenStatus status2) {
		change = new ResultChange(status1.createResult(), status2.createResult());
	}

	public GwenChange has(Line line, StatusChange status) {
		assertThat(change.getStatuses(), hasEntry(line, status));
		return this;
	}

	public GwenChange hasNoDescriptionChangeFor(Line... lines) {
		for (Line line : lines) {
			assertThat(change.getDescriptions(), not(hasKey(line)));
		}
		return this;
	}

	public GwenChange hasDescriptionChangeFor(Line... lines) {
		for (Line line : lines) {
			assertThat(change.getDescriptions(), hasEntry(equalTo(line), not(emptyOrNullString())));
		}
		return this;
	}

	public GwenChange hasNoErrorChange() {
		assertEquals(ErrorChange.NoErrors, change.getError());
		return this;
	}
}
