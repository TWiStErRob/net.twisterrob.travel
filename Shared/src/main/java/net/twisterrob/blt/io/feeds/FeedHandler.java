package net.twisterrob.blt.io.feeds;

import java.io.*;

import org.xml.sax.SAXException;

public interface FeedHandler<T extends BaseFeed<T>> {
	T parse(InputStream is) throws IOException, SAXException;
}
