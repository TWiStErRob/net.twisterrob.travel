package net.twisterrob.android.utils.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.twisterrob.java.io.MailSender;

@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
public class MailSenderAsyncTask extends android.os.AsyncTask<String, Void, Boolean> {
	private static final Logger LOG = LoggerFactory.getLogger(MailSenderAsyncTask.class);

	private final MailSender m = new MailSender();

	public MailSenderAsyncTask(String subject, String from, String... to) {
		m.setTo(to);
		m.setFrom(from);
		m.setSubject(subject);
	}

	@Override protected Boolean doInBackground(String... params) {
		try {
			m.setBody(params[0]);
			m.send();
			return true;
		} catch (Exception ex) {
			LOG.error("Cannot send {}.", m, ex);
			return false;
		}
	}
}
