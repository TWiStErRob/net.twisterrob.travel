package net.twisterrob.blt.io.feeds;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData;
import net.twisterrob.blt.model.Line;

/**
 * From email: Thank you for registering for Transport for London (TfL) syndicated feeds.
 */
public class TFLUrlBuilder implements URLBuilder {
	private final @Nonnull TrackerNetData trackerNetData;
	private String m_email;
	
	public TFLUrlBuilder(String email, @Nonnull TrackerNetData data) {
		trackerNetData = data;
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
			case TubeDepartureBoardsPredictionSummary: {
				Line line = (Line)args.get("line");
				return new URL(feed.getUrl(), trackerNetData.getTrackerNetCodeOf(line));
			}
			case TubeDepartureBoardsPredictionDetailed: {
				Line line = (Line)args.get("line");
				if (Line.UNDERGROUND.contains(line)) {
					String code = trackerNetData.getTrackerNetCodeOf(line);
					return new URL(new URL(feed.getUrl(), code + "/"), (String)args.get("station"));
				} else {
					throw new IllegalArgumentException(line + " line is not underground, cannot request prediction for it.");
				}
			}
			//$CASES-OMITTED$
			default:
				return feed.getUrl();
		}
	}
}
