package net.twisterrob.blt.gapp;

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;
import jakarta.servlet.http.*;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

import net.twisterrob.blt.gapp.viewmodel.*;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;
import net.twisterrob.java.utils.ObjectTools;

import static net.twisterrob.blt.gapp.FeedConsts.*;

@Controller
@SuppressWarnings("serial")
public class LineStatusHistoryServlet {
	private static final Logger LOG = LoggerFactory.getLogger(LineStatusHistoryServlet.class);

	private static final String QUERY_DISPLAY_CURRENT = "current";
	private static final String QUERY_DISPLAY_ERRORS = "errors";
	private static final String QUERY_DISPLAY_MAX = "max";
	private static final int DISPLAY_MAX_DEFAULT = 100;

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@Get("/LineStatusHistory")
	@View("LineStatus")
	@Produces(MediaType.TEXT_HTML)
	public HttpResponse<?> doGet(HttpServletRequest req, HttpServletResponse resp) {
		Feed feed = Feed.TubeDepartureBoardsLineStatus;
		List<Result> results = new LinkedList<>();

		// Params
		int max;
		try {
			max = Integer.parseInt(req.getParameter(QUERY_DISPLAY_MAX));
		} catch (NumberFormatException ex) {
			max = DISPLAY_MAX_DEFAULT;
		}
		if (Boolean.parseBoolean(req.getParameter(QUERY_DISPLAY_CURRENT))) {
			FullEntity<IncompleteKey> entry = downloadNewEntry(datastore, feed);
			results.add(toResult(entry));
		}
		boolean skipErrors = true;
		if (Boolean.parseBoolean(req.getParameter(QUERY_DISPLAY_ERRORS))) {
			skipErrors = false;
		}

		// process them
		Iterator<Entity> entries = fetchEntries(feed);
		while (entries.hasNext()) {
			Entity entry = entries.next();
			if (--max < 0) {
				break; // we've had enough
			}
			Result result = toResult(entry);
			results.add(result);
		}

		List<ResultChange> differences = getDifferences(results, skipErrors);

		return HttpResponse.ok(
				new LineStatusHistoryModel(
						differences,
						new LineColor.AllColors(FeedConsts.STATIC_DATA.getLineColors())
				)
		);
	}

	private record LineStatusHistoryModel(
			List<ResultChange> feedChanges,
			Iterable<LineColor> colors
	) {

	}

	private static Result toResult(BaseEntity<?> entry) {
		Result result;
		String content = DatastoreStatusHistoryRepository.hasProperty(entry, DS_PROP_CONTENT) ? entry.getString(DS_PROP_CONTENT) : null;
		String error = DatastoreStatusHistoryRepository.hasProperty(entry, DS_PROP_ERROR) ? entry.getString(DS_PROP_ERROR) : null;
		Date date = entry.getTimestamp(DS_PROP_RETRIEVED_DATE).toDate();
		if (content != null) {
			try {
				Feed feed = Feed.valueOf(entry.getKey().getKind());
				InputStream stream = new ByteArrayInputStream(content.getBytes(ENCODING));
				@SuppressWarnings({"RedundantTypeArguments", "RedundantSuppression"}) // False positive.
				LineStatusFeed feedContents = feed.<LineStatusFeed>getHandler().parse(stream);
				result = new Result(date, feedContents);
			} catch (Exception ex) {
				result = new Result(date, "Error while displaying loaded XML: " + ObjectTools.getFullStackTrace(ex));
			}
		} else if (error != null) {
			result = new Result(date, error);
		} else {
			result = new Result(date, "Empty entity");
		}
		return result;
	}

	protected Iterator<Entity> fetchEntries(Feed feed) {
		Query<Entity> q = Query
				.newEntityQueryBuilder()
				.setKind(feed.name())
				.addOrderBy(OrderBy.desc(DS_PROP_RETRIEVED_DATE))
				.build()
				;
		QueryResults<Entity> results = datastore.run(q);
		return results;
	}

	private static List<ResultChange> getDifferences(List<Result> results, boolean skipErrors) {
		List<ResultChange> resultChanges = new ArrayList<>(results.size());
		Result newResult = null;
		for (Result oldResult : results) { // we're going forward, but the list is backwards
			if (skipErrors && oldResult.getFullError() != null) {
				continue;
			}
			resultChanges.add(new ResultChange(oldResult, newResult));
			newResult = oldResult;
		}
		resultChanges.add(new ResultChange(null, newResult));
		resultChanges.remove(0);
		return resultChanges;
	}

	public static FullEntity<IncompleteKey> downloadNewEntry(Datastore datastore, Feed feed) {
		KeyFactory keyFactory = datastore.newKeyFactory().setKind(feed.name());
		FullEntity.Builder<IncompleteKey> newEntry = Entity.newBuilder(keyFactory.newKey());
		try {
			String feedResult = HttpStatusInteractor.downloadFeed(feed);
			newEntry.set(DS_PROP_CONTENT, DatastoreStatusHistoryRepository.unindexedString(feedResult));
		} catch (Exception ex) {
			LOG.error("Cannot load '{}'!", feed, ex);
			newEntry.set(DS_PROP_ERROR, DatastoreStatusHistoryRepository.unindexedString(ObjectTools.getFullStackTrace(ex)));
		}
		newEntry.set(DS_PROP_RETRIEVED_DATE, Timestamp.now());
		return newEntry.build();
	}
}
