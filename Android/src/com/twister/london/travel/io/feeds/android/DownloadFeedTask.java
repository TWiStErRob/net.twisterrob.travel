package com.twister.london.travel.io.feeds.android;

import java.io.*;
import java.net.*;

import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.twister.android.utils.concurrent.AsyncTaskResult;
import com.twister.android.utils.log.*;
import com.twister.london.travel.App;
import com.twister.london.travel.io.feeds.*;

public class DownloadFeedTask<T extends BaseFeed> extends AsyncTask<Feed, Integer, AsyncTaskResult<T>> {
	protected static final Log LOG = LogFactory.getLog(Tag.IO);
	protected AsyncTaskResult<T> doInBackground(Feed... feeds) {
		Feed feed;
		if (feeds == null || feeds.length != 1 || (feed = feeds[0]) == null) {
			throw new IllegalArgumentException("Too many feeds, only one is handled, and must be one!");
		}
		try {
			URL url = App.getInstance().getUrls().getFeedUrl(feed);
			LOG.debug("%s", url);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			try {
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
		}
	}
}