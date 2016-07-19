package net.twisterrob.blt.io.feeds;

import java.net.*;
import java.util.Map;

import net.twisterrob.blt.model.Line;

public class LocalhostUrlBuilder implements URLBuilder {
	private static final String LOCALHOST = "http://1.1.1.15:8081/Data/"; // wifi
	//	private static final String LOCALHOST = "http://192.168.43.165:8081/"; // tether local
	@Override public URL getFeedUrl(Feed feed, Map<String, ?> args) throws MalformedURLException {
		String spec;
		if (feed.getType() == Feed.Type.Syndication) {
			spec = "feed.aspx_email=papp.robert.s@gmail.com&feedId=" + feed.getFeedId() + ".xml";
		}
		switch (feed) {
			case TubeDepartureBoardsLineStatus:
				spec = "TrackerNetStatus/LineStatus.xml";
				break;
			case TubeDepartureBoardsLineStatusIncidents:
				spec = "TrackerNetStatus/LineStatus_IncidentsOnly.xml";
				break;
			case TubeDepartureBoardsPredictionSummary:
				spec = String.format("PredictionSummary/PredictionSummary-%s.xml",
						((Line)args.get("line")).getTrackerNetCode());
				break;
			case TubeDepartureBoardsPredictionDetailed:
				spec = String.format("PredictionDetailed/PredictionDetailed-%s-%s.xml",
						((Line)args.get("line")).getTrackerNetCode(), args.get("station"));
				break;
			//$CASES-OMITTED$
			default:
				throw new UnsupportedOperationException(feed + " is not yet supported");
		}
		return new URL(new URL(LOCALHOST), spec);
	}
}
