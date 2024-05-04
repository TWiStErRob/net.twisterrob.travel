package net.twisterrob.android.utils.concurrent;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.AsyncTask;

import net.twisterrob.java.io.FeedbackSender;

@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
public class FeedbackSenderAsyncTask extends android.os.AsyncTask<String, Void, Boolean> {
	private static final Logger LOG = LoggerFactory.getLogger(FeedbackSenderAsyncTask.class);

	@Override protected Boolean doInBackground(String... params) {
		try {
			new FeedbackSender().send(params[0], params[1]);
			return true;
		} catch (Exception ex) {
			LOG.error("Cannot send {}.", Arrays.toString(params), ex);
			return false;
		}
	}
	
	public AsyncTask<String, Void, Boolean> execute(String title, String body) {
		return super.execute(title, body);
	}
}
