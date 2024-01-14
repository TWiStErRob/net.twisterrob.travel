package net.twisterrob.blt.io.feeds;

import java.net.*;
import java.util.*;

import javax.annotation.Nonnull;

import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData;
import net.twisterrob.blt.model.Line;

public class LocalhostUrlBuilder implements URLBuilder {

	private static final String LOCALHOST = "http://10.0.2.2:8081/fake-data/"; // emulator

	private final @Nonnull TrackerNetData trackerNetData;

	public LocalhostUrlBuilder(@Nonnull TrackerNetData data) {
		trackerNetData = data;
	}

	//private static final String LOCALHOST = "http://1.1.1.15:8081/fake-data/"; // wifi
	//private static final String LOCALHOST = "http://192.168.43.165:8081/"; // tether local
	@Nonnull @Override public URL getFeedUrl(Feed feed, Map<String, ?> args) throws MalformedURLException {
		String spec;
		if (feed.getType() == Feed.Type.Syndication) {
			spec = String.format(Locale.ROOT, "feed.aspx_email=%s&feedId=%s.xml",
					"papp.robert.s@gmail.com", feed.getFeedId());
		}
		switch (feed) {
			case TubeDepartureBoardsLineStatus:
				spec = "TrackerNetStatus/LineStatus.xml";
				break;
			case TubeDepartureBoardsLineStatusIncidents:
				spec = "TrackerNetStatus/LineStatus_IncidentsOnly.xml";
				break;
			case TubeDepartureBoardsPredictionSummary:
				spec = String.format(Locale.ROOT, "PredictionSummary/PredictionSummary-%s.xml",
						trackerNetData.getTrackerNetCodeOf((Line)args.get("line")));
				break;
			case TubeDepartureBoardsPredictionDetailed:
				spec = String.format(Locale.ROOT, "PredictionDetailed/PredictionDetailed-%s-%s.xml",
						trackerNetData.getTrackerNetCodeOf((Line)args.get("line")), args.get("station"));
				break;
			//$CASES-OMITTED$
			default:
				throw new UnsupportedOperationException(feed + " is not yet supported");
		}
		return new URL(new URL(LOCALHOST), spec);
	}
}
