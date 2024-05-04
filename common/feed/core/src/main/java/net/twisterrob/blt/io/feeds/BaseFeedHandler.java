package net.twisterrob.blt.io.feeds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.DefaultHandler;

import net.twisterrob.java.exceptions.StackTrace;
import net.twisterrob.java.io.FeedbackSender;

public abstract class BaseFeedHandler<T extends BaseFeed<T>> extends DefaultHandler implements FeedHandler<T> {
	public final Logger LOG = LoggerFactory.getLogger(getClass());

	protected static void sendFeedback(String title, String body) {
		FeedbackSender sender = new FeedbackSender();
		try {
			sender.send(title, body);
		} catch (Exception e) {
			String callingClass = new StackTrace().getStackTrace()[1].getClassName();
			Logger log = LoggerFactory.getLogger(callingClass);
			log.warn("Cannot send email: ", e);
		}
	}
}
