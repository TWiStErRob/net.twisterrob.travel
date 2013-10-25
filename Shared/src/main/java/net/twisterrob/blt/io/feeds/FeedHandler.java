package net.twisterrob.blt.io.feeds;

import java.io.*;

import org.xml.sax.SAXException;

public interface FeedHandler<T> {
	public abstract T parse(InputStream is) throws IOException, SAXException;
}
