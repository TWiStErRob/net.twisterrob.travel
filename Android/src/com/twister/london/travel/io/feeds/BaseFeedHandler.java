package com.twister.london.travel.io.feeds;

import java.io.*;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.twister.android.utils.log.*;

public abstract class BaseFeedHandler<T extends BaseFeed> extends DefaultHandler {
	protected static final Log LOG = LogFactory.getLog(Tag.IO);

	public abstract T parse(InputStream is) throws IOException, SAXException;
}
