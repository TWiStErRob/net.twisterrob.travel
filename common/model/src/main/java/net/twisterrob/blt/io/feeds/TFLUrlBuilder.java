package net.twisterrob.blt.io.feeds;

import java.net.*;
import java.util.*;

import javax.annotation.Nonnull;

import net.twisterrob.blt.model.Line;

/**
 * From email: Thank you for registering for Transport for London (TfL) syndicated feeds.
 */
public class TFLUrlBuilder implements URLBuilder {
	private String m_email;
	public TFLUrlBuilder(String email) {
		m_email = email;
	}

	public URL getSyncdicationFeed(int feedId) throws MalformedURLException {
		String query = String.format(Locale.ROOT, "?email=%s&feedId=%d", m_email, feedId);
		return new URL(Feed.Type.Syndication.getBaseUrl(), query);
	}

	public URL getSyncdicationFeed(Feed feed) throws MalformedURLException {
		if (feed.getType() != Feed.Type.Syndication) {
			throw new IllegalArgumentException("Only syndication feeds are allowed here");
		}
		return getSyncdicationFeed(feed.getFeedId());
	}

	@Nonnull public URL getFeedUrl(Feed feed, Map<String, ?> args) throws MalformedURLException {
		if (feed.getType() == Feed.Type.Syndication) {
			return getSyncdicationFeed(feed);
		}
		switch (feed) {
			case TubeDepartureBoardsPredictionSummary:
				return new URL(feed.getUrl(), ((Line)args.get("line")).getTrackerNetCode());
			case TubeDepartureBoardsPredictionDetailed:
				Line line = (Line)args.get("line");
				if (Line.UNDERGROUND.contains(line)) {
					return new URL(new URL(feed.getUrl(), line.getTrackerNetCode() + "/"), (String)args.get("station"));
				} else {
					return null;
				}
				//$CASES-OMITTED$
			default:
				return feed.getUrl();
		}
	}
}