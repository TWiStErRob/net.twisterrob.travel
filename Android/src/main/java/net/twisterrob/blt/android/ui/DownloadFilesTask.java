package net.twisterrob.blt.android.ui;

import java.io.*;
import java.net.*;

import net.twisterrob.android.utils.concurrent.AsyncTaskResult;
import net.twisterrob.blt.io.feeds.facilities.*;
import net.twisterrob.java.io.IOTools;

import org.xml.sax.SAXException;

import android.os.AsyncTask;

class DownloadFilesTask extends AsyncTask<URL, Integer, AsyncTaskResult<FacilitiesFeed>> {
	@SuppressWarnings("resource")
	@Override
	protected AsyncTaskResult<FacilitiesFeed> doInBackground(URL... urls) {
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
				return new AsyncTaskResult<FacilitiesFeed>(root);
			} finally {
				IOTools.closeConnection(connection, input);
			}
		} catch (IOException ex) {
			return new AsyncTaskResult<FacilitiesFeed>(ex);
		} catch (SAXException ex) {
			return new AsyncTaskResult<FacilitiesFeed>(ex);
		}
	}
}