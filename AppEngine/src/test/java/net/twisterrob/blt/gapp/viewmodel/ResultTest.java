package net.twisterrob.blt.gapp.viewmodel;

import java.util.Date;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;

public class ResultTest {
	@Test public void testSingleLineErrorHeader() {
		String error = "Error message";
		String errorHeader = "Error message";

		Result result = new Result(new Date(), error);

		assertEquals(errorHeader, result.getErrorHeader());
		assertEquals(error, result.getFullError());
	}

	@Test public void testOneLineErrorHeader() {
		String error = "Error message\n";
		String errorHeader = "Error message";

		Result result = new Result(new Date(), error);

		assertEquals(errorHeader, result.getErrorHeader());
		assertEquals(error, result.getFullError());
	}

	@Test public void testMultiLineErrorHeader() {
		String error = "Error message\nSecond line\nThird line";
		String errorHeader = "Error message";

		Result result = new Result(new Date(), error);

		assertEquals(errorHeader, result.getErrorHeader());
		assertEquals(error, result.getFullError());
	}

	@Test(expected = NullPointerException.class) public void testErrorMustBeNotNull() {
		Result result = new Result(new Date(), (String)null);
	}

	@Test public void testConsistentPropertiesError() {
		String error = "error";
		Date date = mock(Date.class);

		Result result = new Result(date, error);

		assertEquals(error, result.getFullError());
		assertEquals(date, result.getWhen());
	}

	@Test public void testConsistentPropertiesFeed() {
		LineStatusFeed feed = mock(LineStatusFeed.class);
		Date date = mock(Date.class);

		Result result = new Result(date, feed);

		assertEquals(feed, result.getContent());
		assertEquals(date, result.getWhen());
	}
}
