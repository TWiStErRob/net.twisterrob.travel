package net.twisterrob.java.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FeedbackSender {

	public void send(@Nullable String title, @Nonnull String body) throws IOException {
		try {
			URL url = buildUrl(title);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.connect();
			try {
				IOTools.writeAll(conn.getOutputStream(), body);
				InputStream response = new BufferedInputStream(conn.getInputStream());
				String result = IOTools.readAll(response, "UTF-8");
				if (!result.trim().isEmpty()) {
					throw new IOException("Server responded with: " + result);
				}
			} finally {
				conn.disconnect();
			}
		} catch (IOException ex) {
			throw new IOException("Cannot send " + title, ex);
		}
	}

	private static @Nonnull URL buildUrl(String title)
			throws UnsupportedEncodingException, MalformedURLException {
		String titleQuery;
		if (title != null) {
			String escapedTitle = URLEncoder.encode(title, "UTF-8");
			titleQuery = "title=" + escapedTitle;
		} else {
			titleQuery = "";
		}
		return new URL(
				"https://twisterrob-london.appspot.com/InternalFeedback?"
						+ titleQuery
		);
	}
}
