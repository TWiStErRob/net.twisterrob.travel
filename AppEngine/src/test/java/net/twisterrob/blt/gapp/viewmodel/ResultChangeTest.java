package net.twisterrob.blt.gapp.viewmodel;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.twisterrob.blt.gapp.viewmodel.ResultChange.*;

public class ResultChangeTest {
	@Test public void testConsistentProperties() {
		Result result1 = mock(Result.class);
		Result result2 = mock(Result.class);

		ResultChange change = new ResultChange(result1, result2);

		assertEquals(result1, change.getOld());
		assertEquals(result2, change.getNew());
	}

	@Test public void testStatusChange() {
		for (StatusChange change : StatusChange.values()) {
			assertThat(change.name(), change.getTitle(), is(notNullValue()));
			assertThat(change.name(), change.getCssClass(), is(not(emptyOrNullString())));
		}
	}

	@Test public void testErrorChange() {
		for (ErrorChange change : ErrorChange.values()) {
			assertThat(change.name(), change.getTitle(), is(notNullValue()));
		}
	}
}
