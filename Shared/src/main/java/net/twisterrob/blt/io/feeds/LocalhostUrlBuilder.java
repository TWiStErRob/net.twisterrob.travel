package net.twisterrob.blt.io.feeds;

import java.net.*;

public class LocalhostUrlBuilder implements URLBuilder {
	private static final String LOCALHOST = "http://192.168.0.5:8081/";
	@Override
	public URL getFeedUrl(Feed feed) throws MalformedURLException {
		String spec;
		if (feed.getType() == Feed.Type.Syndication) {
			spec = "feed.aspx_email=papp.robert.s@gmail.com&feedId=" + feed.getFeedId() + ".xml";
		}
		switch (feed) {
			case TubeDepartureBoardsLineStatus:
				spec = "LineStatus.xml";
				break;
			case TubeDepartureBoardsLineStatusIncidents:
				spec = "LineStatus_IncidentsOnly.xml";
				break;
			default:
				throw new UnsupportedOperationException(feed + " is not yet supported");
		}
		return new URL(new URL(LOCALHOST), spec);
	}
}
