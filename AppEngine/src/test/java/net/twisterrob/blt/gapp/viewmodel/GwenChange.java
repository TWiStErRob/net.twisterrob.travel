package net.twisterrob.blt.gapp.viewmodel;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import com.shazam.gwen.collaborators.*;

import net.twisterrob.blt.gapp.viewmodel.ResultChange.*;
import net.twisterrob.blt.model.Line;

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
