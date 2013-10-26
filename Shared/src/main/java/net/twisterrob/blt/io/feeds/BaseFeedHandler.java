package net.twisterrob.blt.io.feeds;

import net.twisterrob.android.mail.MailSender;

import org.slf4j.*;
import org.xml.sax.helpers.DefaultHandler;

public abstract class BaseFeedHandler<T extends BaseFeed> extends DefaultHandler implements FeedHandler<T> {
	public final Logger LOG = LoggerFactory.getLogger(getClass());

	protected static void sendMail(String body) {
		MailSender sender = new MailSender("*********@********.****", "*********");
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
