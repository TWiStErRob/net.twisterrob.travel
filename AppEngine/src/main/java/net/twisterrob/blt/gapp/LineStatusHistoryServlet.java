package net.twisterrob.blt.gapp;

import java.io.*;
import java.util.*;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import org.apache.tools.ant.filters.StringInputStream;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

import net.twisterrob.blt.gapp.viewmodel.*;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;
import net.twisterrob.java.utils.ObjectTools;

import static net.twisterrob.blt.gapp.FeedConsts.*;

@Controller
@SuppressWarnings("serial")
public class LineStatusHistoryServlet extends HttpServlet {
	//private static final Logger LOG = LoggerFactory.getLogger(LineStatusHistoryServlet.class);

	private static final String QUERY_DISPLAY_CURRENT = "current";
	private static final String QUERY_DISPLAY_ERRORS = "errors";
	private static final String QUERY_DISPLAY_MAX = "max";
	private static final int DISPLAY_MAX_DEFAULT = 100;

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@Get("/LineStatusHistory")
	@Produces(MediaType.TEXT_HTML)
	@Override public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
			FullEntity<IncompleteKey> entry = FeedCronServlet.downloadNewEntry(datastore, feed);
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

		// display them
		req.setAttribute("feedChanges", differences);
		req.setAttribute("colors", new LineColor.AllColors(FeedConsts.STATIC_DATA.getLineColors()));
		RequestDispatcher view = req.getRequestDispatcher("/LineStatus.jsp");
		view.forward(req, resp);
	}

	private static Result toResult(BaseEntity<?> entry) {
		Result result;
		String content = entry.getString(DS_PROP_CONTENT);
		String error = entry.getString(DS_PROP_ERROR);
		Date date = entry.getTimestamp(DS_PROP_RETRIEVED_DATE).toDate();
		if (content != null) {
			try {
				Feed feed = Feed.valueOf(entry.getKey().getKind());
				InputStream stream = new StringInputStream(content, ENCODING);
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
}
