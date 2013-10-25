package net.twisterrob.blt.data.io;

import java.io.*;

import net.twisterrob.blt.io.feeds.timetable.*;

import org.xml.sax.SAXException;

public class FeedReader {
	public JourneyPlannerTimetableFeed readFeed(String root, Iterable<String> fileNames) throws IOException,
			SAXException {
		JourneyPlannerTimetableFeed feed = null;
		for (String fileName: fileNames) {
			File file = new File(root, fileName);
			JourneyPlannerTimetableFeed newFeed = readFeed(file);
			if (feed == null) {
				feed = newFeed;
			} else {
				feed.merge(newFeed);
			}
		}
		return feed;
	}

	@SuppressWarnings("static-method")
	public JourneyPlannerTimetableFeed readFeed(File file) throws IOException, SAXException {
		JourneyPlannerTimetableHandler handler = new JourneyPlannerTimetableHandler();
		try (FileInputStream stream = new FileInputStream(file)) {
			JourneyPlannerTimetableFeed feed = handler.parse(stream);
			return feed;
		}
	}
}
