package net.twisterrob.blt.android.io.feeds;

import java.io.*;
import java.net.*;
import java.util.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.android.utils.log.*;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.java.io.IOTools;

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
		HttpURLConnection connection = null;
		InputStream input = null;
		try {
			if (feeds == null || feeds.length != 1 || feeds[0] == null) {
				throw new IllegalArgumentException("Too many feeds, only one is handled, and must be one!");
			}

			Feed feed = feeds[0];
			URL url = App.getInstance().getUrls().getFeedUrl(feed, m_args);
			LOG.debug("%s", url);

			connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(1000);
			connection.setReadTimeout(2000);
			connection.connect();
			input = connection.getInputStream();

			@SuppressWarnings("unchecked")
			T root = (T)feed.getHandler().parse(input);
			return new AsyncTaskResult<T>(root);
		} catch (IOException ex) {
			return new AsyncTaskResult<T>(ex);
		} catch (SAXException ex) {
			return new AsyncTaskResult<T>(ex);
		} catch (Exception ex) {
			return new AsyncTaskResult<T>(ex);
		} finally {
			IOTools.closeConnection(connection, input);
		}
	}
}