package net.twisterrob.blt.io.feeds;

import org.slf4j.*;
import org.xml.sax.helpers.DefaultHandler;

import net.twisterrob.java.exceptions.StackTrace;
import net.twisterrob.java.io.MailSender;

public abstract class BaseFeedHandler<T extends BaseFeed<T>> extends DefaultHandler implements FeedHandler<T> {
	public final Logger LOG = LoggerFactory.getLogger(getClass());

	protected static void sendMail(String body) {
		MailSender sender = new MailSender();
		sender.setSubject("Better London Travel");
		sender.setFrom("better-london-travel@twisterrob.net");
		sender.setTos("papp.robert.s@gmail.com");
		sender.setBody(body);
		try {
			sender.send();
		} catch (Exception e) {
			String callingClass = new StackTrace().getStackTrace()[1].getClassName();
			Logger log = LoggerFactory.getLogger(callingClass);
			log.warn("Cannot send email: ", e);
		}
	}
}
