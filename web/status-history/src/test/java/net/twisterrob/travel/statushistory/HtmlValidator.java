package net.twisterrob.travel.statushistory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

import static org.junit.Assert.assertEquals;

public class HtmlValidator {

	public static void assertValidHtml(String html) {
		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(true);

		List<TidyMessage> messages = new ArrayList<>();
		tidy.setMessageListener(messages::add);
		tidy.parse(new StringReader(html), new StringWriter());
		assertEquals(html + "\n" + messages, 0, messages.size());
	}
}
