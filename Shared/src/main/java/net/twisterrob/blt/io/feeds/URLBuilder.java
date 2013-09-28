package net.twisterrob.blt.io.feeds;

import java.net.*;

public interface URLBuilder {
	URL getFeedUrl(Feed feed) throws MalformedURLException;
}
