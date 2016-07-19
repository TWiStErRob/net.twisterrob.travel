package net.twisterrob.blt.io.feeds;

import java.net.*;
import java.util.Map;

import javax.annotation.Nonnull;

public interface URLBuilder {
	@Nonnull URL getFeedUrl(Feed feed, Map<String, ?> args) throws MalformedURLException;
}
