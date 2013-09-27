package com.twister.london.travel.ui;

import java.io.*;
import java.net.*;

import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.twister.android.utils.concurrent.AsyncTaskResult;
import com.twister.london.travel.io.feeds.*;

class DownloadFilesTask extends AsyncTask<URL, Integer, AsyncTaskResult<FacilitiesFeed>> {
	protected AsyncTaskResult<FacilitiesFeed> doInBackground(URL... urls) {
		if (urls.length != 1 || urls[0] == null) {
			throw new IllegalArgumentException("Too many urls, only one is handled, and must be one!");
		}
		try {
			URL url = urls[0];
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			try {
				connection.connect();
				InputStream input = connection.getInputStream();

				FacilitiesFeed root = new FacilitiesFeedHandler().parse(input);
				return new AsyncTaskResult<FacilitiesFeed>(root);
			} finally {
				connection.disconnect();
			}
		} catch (IOException ex) {
			return new AsyncTaskResult<FacilitiesFeed>(ex);
		} catch (SAXException ex) {
			return new AsyncTaskResult<FacilitiesFeed>(ex);
		}
	}
}