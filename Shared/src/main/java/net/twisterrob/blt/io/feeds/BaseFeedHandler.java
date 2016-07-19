package net.twisterrob.blt.io.feeds;

import org.slf4j.*;
import org.xml.sax.helpers.DefaultHandler;

import net.twisterrob.java.io.MailSender;

public abstract class BaseFeedHandler<T extends BaseFeed<T>> extends DefaultHandler implements FeedHandler<T> {
	public final Logger LOG = LoggerFactory.getLogger(getClass());

	protected static void sendMail(String body) {
		MailSender sender = new MailSender();
		sender.setSubject("Better London Travel");
		sender.setFrom("better-london-travel@twisterrob.net");
		sender.setTo("papp.robert.s@gmail.com");
		sender.setBody(body);
		try {
			sender.send();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
