package net.twisterrob.blt.io.feeds;

import java.net.*;
import java.util.Map;

public interface URLBuilder {
	/**
	 * @param args 
	 * @return never null
	 * @throws MalformedURLException
	 */
	URL getFeedUrl(Feed feed, Map<String, ?> args) throws MalformedURLException;
}
