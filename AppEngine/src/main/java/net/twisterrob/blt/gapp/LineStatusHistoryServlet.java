package net.twisterrob.blt.gapp;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.tools.ant.filters.StringInputStream;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;

import net.twisterrob.blt.gapp.viewmodel.*;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;
import net.twisterrob.java.utils.ObjectTools;

import static net.twisterrob.blt.gapp.FeedConsts.*;

@SuppressWarnings("serial")
public class LineStatusHistoryServlet extends HttpServlet {
	//private static final Logger LOG = LoggerFactory.getLogger(LineStatusHistoryServlet.class);

	private static final String QUERY_DISPLAY_CURRENT = "current";
	private static final String QUERY_DISPLAY_ERRORS = "errors";
	private static final String QUERY_DISPLAY_MAX = "max";
	private static final int DISPLAY_MAX_DEFAULT = 100;

	private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

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
			Entity entry = FeedCronServlet.downloadNewEntry(feed);
			results.add(toResult(entry));
		}
		boolean skipErrors = true;
		if (Boolean.parseBoolean(req.getParameter(QUERY_DISPLAY_ERRORS))) {
			skipErrors = false;
		}

		// process them
		Iterable<Entity> entries = fetchEntries(feed);
		for (Entity entry : entries) {
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

	private static Result toResult(Entity entry) {
		Result result;
		Text content = (Text)entry.getProperty(DS_PROP_CONTENT);
		Text error = (Text)entry.getProperty(DS_PROP_ERROR);
		Date date = (Date)entry.getProperty(DS_PROP_RETRIEVED_DATE);
		if (content != null) {
			try {
				Feed feed = Feed.valueOf(entry.getKind());
				InputStream stream = new StringInputStream(content.getValue(), ENCODING);
				LineStatusFeed feedContents = feed.<LineStatusFeed>getHandler().parse(stream);
				result = new Result(date, feedContents);
			} catch (Exception ex) {
				result = new Result(date, "Error while displaying loaded XML: " + ObjectTools.getFullStackTrace(ex));
			}
		} else if (error != null) {
			result = new Result(date, error.getValue());
		} else {
			result = new Result(date, "Empty entity");
		}
		return result;
	}

	protected Iterable<Entity> fetchEntries(Feed feed) {
		Query q = new Query(feed.name()).addSort(DS_PROP_RETRIEVED_DATE, SortDirection.DESCENDING);
		Iterable<Entity> results = datastore.prepare(q).asIterable();
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
