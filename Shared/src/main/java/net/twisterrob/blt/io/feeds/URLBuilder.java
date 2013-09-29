package net.twisterrob.blt.io.feeds;

import java.net.*;

public interface URLBuilder {
	/**
	 * @return never null
	 * @throws MalformedURLException
	 */
	URL getFeedUrl(Feed feed) throws MalformedURLException;
}
