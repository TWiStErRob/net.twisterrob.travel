package net.twisterrob.blt.android.ui;

import java.io.*;
import java.net.*;

import org.xml.sax.SAXException;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.io.feeds.facilities.*;
import net.twisterrob.java.io.IOTools;

@SuppressWarnings("deprecation") // https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
class DownloadFilesTask extends android.os.AsyncTask<URL, Integer, AsyncTaskResult<URL, FacilitiesFeed>> {
	@SuppressWarnings("resource")
	@Override protected AsyncTaskResult<URL, FacilitiesFeed> doInBackground(URL... urls) {
		if (urls.length != 1 || urls[0] == null) {
			throw new IllegalArgumentException("Too many urls, only one is handled, and must be one!");
		}
		try {
			URL url = urls[0];
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			InputStream input = null;
			try {
				connection.connect();
				input = connection.getInputStream();

				FacilitiesFeed root = new FacilitiesFeedHandler().parse(input);
				return new AsyncTaskResult<>(root);
			} finally {
				IOTools.closeConnection(connection, input);
			}
		} catch (IOException ex) {
			return new AsyncTaskResult<>(ex);
		} catch (SAXException ex) {
			return new AsyncTaskResult<>(ex);
		}
	}
}
