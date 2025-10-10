package net.twisterrob.blt.io.feeds;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.Nonnull;

public interface URLBuilder {
	@Nonnull URI getFeedUrl(Feed feed, Map<String, ?> args) throws URISyntaxException;
}
