package net.twisterrob.blt.gapp.viewmodel;

import java.util.Date;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;

import static net.twisterrob.blt.gapp.viewmodel.ResultChange.*;

public class ResultChangeTest_Errors {
	@Test public void testErrorChange() {
		Result result1 = new Result(new Date(), "error1");
		Result result2 = new Result(new Date(), "error2");

		ResultChange change = new ResultChange(result1, result2);

		assertErrorAndNoChanges(change, ErrorChange.Change);
	}

	@Test public void testErrorNoChange() {
		Result result1 = new Result(new Date(), "error");
		Result result2 = new Result(new Date(), "error");

		ResultChange change = new ResultChange(result1, result2);

		assertErrorAndNoChanges(change, ErrorChange.Same);
	}

	@Test public void testErrorIntroduced() {
		Result result1 = new Result(new Date(), (LineStatusFeed)null);
		Result result2 = new Result(new Date(), "error");

		ResultChange change = new ResultChange(result1, result2);

		assertErrorAndNoChanges(change, ErrorChange.Failed);
	}

	@Test public void testErrorDisappeared() {
		Result result1 = new Result(new Date(), "error");
		Result result2 = new Result(new Date(), (LineStatusFeed)null);

		ResultChange change = new ResultChange(result1, result2);

		assertErrorAndNoChanges(change, ErrorChange.Fixed);
	}

	@Test public void testErrorNone() {
		Result result1 = new Result(new Date(), (LineStatusFeed)null);
		Result result2 = new Result(new Date(), (LineStatusFeed)null);

		ResultChange change = new ResultChange(result1, result2);

		assertErrorAndNoChanges(change, ErrorChange.NoErrors);
	}

	@Test public void testFirstOne() {
		Result result = mock(Result.class);

		ResultChange change = new ResultChange(null, result);

		assertErrorAndNoChanges(change, ErrorChange.NewStatus);
	}

	@Test public void testLastOne() {
		Result result = mock(Result.class);

		ResultChange change = new ResultChange(result, null);

		assertErrorAndNoChanges(change, ErrorChange.LastStatus);
	}

	@Test public void testMissingResults() {
		ResultChange change = new ResultChange(null, null);

		assertErrorAndNoChanges(change, ErrorChange.NoErrors);
	}

	@Test public void testErrorNewFeedMissing() {
		Result result1 = new Result(new Date(), mock(LineStatusFeed.class));
		Result result2 = new Result(new Date(), (LineStatusFeed)null);

		ResultChange change = new ResultChange(result1, result2);

		assertErrorAndNoChanges(change, ErrorChange.NoErrors);
	}
	@Test public void testErrorOldFeedMissing() {
		Result result1 = new Result(new Date(), mock(LineStatusFeed.class));
		Result result2 = new Result(new Date(), (LineStatusFeed)null);

		ResultChange change = new ResultChange(result2, result1);

		assertErrorAndNoChanges(change, ErrorChange.NoErrors);
	}

	private static void assertErrorAndNoChanges(ResultChange result, ErrorChange errors) {
		assertEquals(errors, result.getError());
		assertThat(result.getStatuses(), is(anEmptyMap()));
		assertThat(result.getDescriptions(), is(anEmptyMap()));
	}
}
