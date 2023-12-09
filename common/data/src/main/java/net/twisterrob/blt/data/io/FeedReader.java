package net.twisterrob.blt.data.io;

import java.io.*;

import org.xml.sax.SAXException;

import net.twisterrob.blt.io.feeds.*;

// TODO move to the shared and use it from DownloadFeed
public class FeedReader<T extends BaseFeed<T>> {
	public T readFeed(Feed feed, String root, Iterable<String> fileNames) throws IOException, SAXException {
		T feedData = null;
		for (String fileName : fileNames) {
			File file = new File(root, fileName);
			T newFeed = readFeed(feed, file);
			if (feedData == null) {
				feedData = newFeed;
			} else {
				feedData = feedData.merge(newFeed);
			}
		}
		return feedData;
	}

	public T readFeed(Feed feed, File file) throws IOException, SAXException {
		FeedHandler<T> handler = feed.getHandler();
		try (FileInputStream stream = new FileInputStream(file)) {
			T feedData = handler.parse(stream);
			return feedData;
		}
	}
}
