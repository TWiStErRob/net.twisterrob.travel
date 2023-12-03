package net.twisterrob.blt.android.io.feeds;

import java.io.*;
import java.net.*;
import java.util.*;

import org.slf4j.*;
import org.xml.sax.SAXException;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.java.io.IOTools;

@SuppressWarnings("deprecation") // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
public class DownloadFeedTask<T extends BaseFeed<T>> extends android.os.AsyncTask<Feed, Integer, AsyncTaskResult<Feed, T>> {
	protected final Logger LOG = LoggerFactory.getLogger(getClass());

	private Map<String, ?> m_args;
	public DownloadFeedTask() {
		this(null);
	}
	public DownloadFeedTask(Map<String, ?> args) {
		m_args = args != null? args : Collections.<String, Object>emptyMap();
	}

	@SuppressWarnings("resource")
	@Override protected AsyncTaskResult<Feed, T> doInBackground(Feed... feeds) {
		HttpURLConnection connection = null;
		InputStream input = null;
		try {
			if (feeds == null || feeds.length != 1 || feeds[0] == null) {
				throw new IllegalArgumentException("Too many feeds, only one is handled, and must be one!");
			}

			Feed feed = feeds[0];
			URL url = App.getInstance().getUrls().getFeedUrl(feed, m_args);
			LOG.debug("{}", url);

			connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(1000);
			connection.setReadTimeout(2000);
			connection.connect();
			input = connection.getInputStream();

			T root = feed.<T>getHandler().parse(input);
			return new AsyncTaskResult<>(root);
		} catch (IOException ex) {
			return new AsyncTaskResult<>(ex);
		} catch (SAXException ex) {
			return new AsyncTaskResult<>(ex);
		} catch (Exception ex) {
			return new AsyncTaskResult<>(ex);
		} finally {
			IOTools.closeConnection(connection, input);
		}
	}
}
