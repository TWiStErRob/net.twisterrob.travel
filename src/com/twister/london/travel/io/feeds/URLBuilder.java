package com.twister.london.travel.io.feeds;

import java.net.*;

public interface URLBuilder {
	URL getFeedUrl(Feed feed) throws MalformedURLException;
}
