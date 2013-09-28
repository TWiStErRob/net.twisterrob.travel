package com.twister.london.travel.io.feeds;

import java.io.*;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.twister.android.mail.MailSender;
import com.twister.android.utils.log.*;

public abstract class BaseFeedHandler<T extends BaseFeed> extends DefaultHandler {
	protected static final Log LOG = LogFactory.getLog(Tag.IO);

	public abstract T parse(InputStream is) throws IOException, SAXException;

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
