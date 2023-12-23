package net.twisterrob.blt.gapp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.Nonnull;

import kotlin.text.Charsets;

import net.twisterrob.travel.domain.london.status.Feed;
import net.twisterrob.travel.domain.london.status.StatusContent;
import net.twisterrob.travel.domain.london.status.api.FeedParser;

public class FeedHandlerFeedParser implements FeedParser {

	@Override public @Nonnull Object parse(@Nonnull Feed feed, @Nonnull StatusContent content) throws Exception {
		try (InputStream stream = new ByteArrayInputStream(content.getContent().getBytes(Charsets.UTF_8))) {
			return net.twisterrob.blt.io.feeds.Feed.valueOf(feed.name()).getHandler().parse(stream);
		}
	}
}
