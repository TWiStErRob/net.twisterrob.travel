package net.twisterrob.blt.gapp;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.http.*;

import org.slf4j.*;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;

import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.java.io.IOTools;
import net.twisterrob.java.utils.ObjectTools;

import static net.twisterrob.blt.gapp.FeedConsts.*;

@SuppressWarnings("serial")
public class FeedCronServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(FeedCronServlet.class);

	private static final String QUERY_FEED = "feed";

	private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	@Override public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String feedString = String.valueOf(req.getParameter(QUERY_FEED));
		Feed feed;
		try {
			feed = Feed.valueOf(feedString);
		} catch (IllegalArgumentException ex) {
			String message = String.format(Locale.getDefault(), "No such feed: '%s'.", feedString);
			LOG.warn(message);
			resp.getWriter().println(message);
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Marker marker = MarkerFactory.getMarker(feed.name());

		Entity newEntry = downloadNewEntry(feed);
		Entity oldEntry = readLatest(datastore, feed);
		if (oldEntry != null) {
			if (sameProp(DS_PROP_CONTENT, oldEntry, newEntry)) {
				LOG.info(marker, "They have the same content.");
				resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else if (sameProp(DS_PROP_ERROR, oldEntry, newEntry)) {
				LOG.info(marker, "They have the same error.");
				resp.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
			} else {
				LOG.info(marker, "They're different, storing...");
				resp.setStatus(HttpServletResponse.SC_ACCEPTED);
				datastore.put(newEntry);
			}
		} else {
			LOG.info(marker, "It's new, storing...");
			resp.setStatus(HttpServletResponse.SC_CREATED);
			datastore.put(newEntry);
		}
	}

	private static Entity readLatest(DatastoreService datastore, Feed feed) {
		// we're only concerned about the latest one, if any
		Query q = new Query(feed.name()).addSort(DS_PROP_RETRIEVED_DATE, SortDirection.DESCENDING);
		Iterator<Entity> result = datastore.prepare(q).asIterator();
		return result.hasNext()? result.next() : null;
	}

	private static boolean sameProp(String propName, Entity oldEntry, Entity newEntry) {
		return oldEntry.hasProperty(propName) && newEntry.hasProperty(propName)
				&& ObjectTools.equals(oldEntry.getProperty(propName), newEntry.getProperty(propName));
	}

	public static Entity downloadNewEntry(Feed feed) {
		Entity newEntry = new Entity(feed.name());
		try {
			String feedResult = downloadFeed(feed);
			newEntry.setProperty(DS_PROP_CONTENT, new Text(feedResult));
		} catch (Exception ex) {
			LOG.error("Cannot load '{}'!", feed, ex);
			newEntry.setProperty(DS_PROP_ERROR, new Text(ObjectTools.getFullStackTrace(ex)));
		}
		newEntry.setProperty(DS_PROP_RETRIEVED_DATE, new Date());
		return newEntry;
	}

	public static String downloadFeed(Feed feed) throws IOException {
		InputStream input = null;
		String result;
		try {
			URL url = URL_BUILDER.getFeedUrl(feed, Collections.<String, Object>emptyMap());
			LOG.debug("Requesting feed '{}': '{}'...", feed, url);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.connect();
			input = connection.getInputStream();
			result = IOTools.readAll(input);
		} finally {
			IOTools.ignorantClose(input);
		}
		return result;
	}
}
