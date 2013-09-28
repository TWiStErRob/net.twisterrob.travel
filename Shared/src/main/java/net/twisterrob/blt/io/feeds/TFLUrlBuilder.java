package net.twisterrob.blt.io.feeds;

import java.net.*;

/**
 * From email: Thank you for registering for Transport for London (TfL) syndicated feeds.
 * 
 * @author TWiStEr
 */
public class TFLUrlBuilder implements URLBuilder {
	private String m_email;
	public TFLUrlBuilder(String email) {
		m_email = email;
	}

	public URL getSyncdicationFeed(int feedId) throws MalformedURLException {
		return new URL(Feed.Type.Syndication.getBaseUrl(), String.format("?email=%s&feedId=%d", m_email, feedId));
	}

	public URL getSyncdicationFeed(Feed feed) throws MalformedURLException {
		if (feed.getType() != Feed.Type.Syndication) {
			throw new IllegalArgumentException("Only syndication feeds are allowed here");
		}
		return getSyncdicationFeed(feed.getFeedId());
	}

	public URL getFeedUrl(Feed feed) throws MalformedURLException {
		if (feed.getType() == Feed.Type.Syndication) {
			return getSyncdicationFeed(feed);
		} else {
			return feed.getUrl();
		}
	}
}
