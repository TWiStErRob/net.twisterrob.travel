package net.twisterrob.blt.gapp;

import java.io.*;
import java.util.*;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.servlet.http.*;

import org.slf4j.*;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.java.utils.ObjectTools;

import static net.twisterrob.blt.gapp.FeedConsts.*;

@Controller
@SuppressWarnings("serial")
public class FeedCronServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(FeedCronServlet.class);

	private static final String QUERY_FEED = "feed";

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@Get("/FeedCron")
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

		FullEntity<IncompleteKey> newEntry = downloadNewEntry(datastore, feed);
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

	private static Entity readLatest(Datastore datastore, Feed feed) {
		Query<Entity> q = Query
				.newEntityQueryBuilder()
				.setKind(feed.name())
				.addOrderBy(OrderBy.desc(DS_PROP_RETRIEVED_DATE))
				.build()
				;
		// We're only concerned about the latest one, if any.
		QueryResults<Entity> result = datastore.run(q);
		return result.hasNext()? result.next() : null;
	}

	private static boolean sameProp(String propName, BaseEntity<?> oldEntry, BaseEntity<?> newEntry) {
		return hasProperty(oldEntry, propName) && hasProperty(newEntry, propName)
				&& ObjectTools.equals(oldEntry.getValue(propName), newEntry.getValue(propName));
	}

	static boolean hasProperty(BaseEntity<?> entry, String propName) {
		return entry.getProperties().containsKey(propName);
	}

	public static FullEntity<IncompleteKey> downloadNewEntry(Datastore datastore, Feed feed) {
		KeyFactory keyFactory = datastore.newKeyFactory().setKind(feed.name());
		FullEntity.Builder<IncompleteKey> newEntry = Entity.newBuilder(keyFactory.newKey());
		try {
			String feedResult = HttpStatusInteractor.downloadFeed(feed);
			newEntry.set(DS_PROP_CONTENT, unindexedString(feedResult));
		} catch (Exception ex) {
			LOG.error("Cannot load '{}'!", feed, ex);
			newEntry.set(DS_PROP_ERROR, unindexedString(ObjectTools.getFullStackTrace(ex)));
		}
		newEntry.set(DS_PROP_RETRIEVED_DATE, Timestamp.now());
		return newEntry.build();
	}

	/**
	 * Strings have a limitation of 1500 bytes when indexed. This removes that limitation.
	 * @see https://cloud.google.com/datastore/docs/concepts/entities#text_string
	 */
	private static Value<?> unindexedString(String value) {
		return value == null
				? NullValue.of()
				: StringValue.newBuilder(value).setExcludeFromIndexes(true).build();
	}

}
