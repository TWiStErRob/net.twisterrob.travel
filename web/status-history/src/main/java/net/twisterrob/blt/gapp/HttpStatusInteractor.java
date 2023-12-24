package net.twisterrob.blt.gapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kotlinx.datetime.Clock;
import kotlinx.datetime.Instant;

import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.java.io.IOTools;
import net.twisterrob.java.utils.ObjectTools;
import net.twisterrob.travel.domain.london.status.Stacktrace;
import net.twisterrob.travel.domain.london.status.StatusContent;
import net.twisterrob.travel.domain.london.status.StatusItem;
import net.twisterrob.travel.domain.london.status.api.StatusInteractor;

import static net.twisterrob.blt.gapp.FeedConsts.URL_BUILDER;

class HttpStatusInteractor implements StatusInteractor {
	private static final Logger LOG = LoggerFactory.getLogger(HttpStatusInteractor.class);

	@Override public @Nonnull StatusItem getCurrent(@Nonnull net.twisterrob.travel.domain.london.status.Feed feed) {
		Instant now = Clock.System.INSTANCE.now();
		try {
			StatusContent content = new StatusContent(downloadFeed(Feed.valueOf(feed.name())));
			return new StatusItem.SuccessfulStatusItem(feed, content, now);
		} catch (Exception ex) {
			LOG.error("Cannot load '{}'!", feed, ex);
			Stacktrace stacktrace = new Stacktrace(ObjectTools.getFullStackTrace(ex));
			return new StatusItem.FailedStatusItem(feed, stacktrace, now);
		}
	}

	public static String downloadFeed(Feed feed) throws IOException {
		InputStream input = null;
		String result;
		try {
			URL url = URL_BUILDER.getFeedUrl(feed, Collections.<String, Object>emptyMap());
			LOG.debug("Requesting feed '{}': '{}'...", feed, url);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.connect();
			input = connection.getInputStream();
			result = IOTools.readAll(input);
		} finally {
			IOTools.ignorantClose(input);
		}
		return result;
	}
}
