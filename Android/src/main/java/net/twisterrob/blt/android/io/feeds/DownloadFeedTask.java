package net.twisterrob.blt.android.io.feeds;

import java.io.*;
import java.net.*;
import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.android.utils.log.*;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.io.feeds.*;

import org.xml.sax.SAXException;

import android.os.AsyncTask;

public class DownloadFeedTask<T extends BaseFeed> extends AsyncTask<Feed, Integer, AsyncTaskResult<T>> {
	protected static final Log LOG = LogFactory.getLog(Tag.IO);
	private Map<String, ?> m_args;
	public DownloadFeedTask() {
		this(null);
	}
	public DownloadFeedTask(Map<String, ?> args) {
		if (args == null) {
			args = Collections.emptyMap();
		}
		m_args = args;
	}
	protected AsyncTaskResult<T> doInBackground(Feed... feeds) {
		Feed feed;
		if (feeds == null || feeds.length != 1 || (feed = feeds[0]) == null) {
			throw new IllegalArgumentException("Too many feeds, only one is handled, and must be one!");
		}
		try {
			URL url = App.getInstance().getUrls().getFeedUrl(feed, m_args);
			LOG.debug("%s", url);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			try {
				connection.setConnectTimeout(1000);
				connection.setReadTimeout(2000);
				connection.connect();
				InputStream input = connection.getInputStream();

				@SuppressWarnings("unchecked")
				T root = (T)feed.getHandler().parse(input);
				return new AsyncTaskResult<T>(root);
			} finally {
				connection.disconnect();
			}
		} catch (IOException ex) {
			return new AsyncTaskResult<T>(ex);
		} catch (SAXException ex) {
			return new AsyncTaskResult<T>(ex);
		} catch (Exception ex) {
			return new AsyncTaskResult<T>(ex);
		}
	}
}