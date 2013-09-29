package net.twisterrob.blt.gapp;
import static net.twisterrob.blt.gapp.FeedConsts.*;

import java.io.IOException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import name.fraser.neil.plaintext.*;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.*;
import net.twisterrob.java.utils.ObjectTools;
import net.twisterrob.java.web.InvokerMap;

import org.apache.tools.ant.filters.StringInputStream;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;

@SuppressWarnings("serial")
public class LineStatusHistoryServlet extends HttpServlet {
	//private static final Logger LOG = LoggerFactory.getLogger(LineStatusHistoryServlet.class);

	private static final String QUERY_DISPLAY_CURRENT = "current";
	private static final String QUERY_DISPLAY_ERRORS = "errors";
	private static final String QUERY_DISPLAY_MAX = "max";
	private static final int DISPLAY_MAX_DEFAULT = 100;

	private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Feed feed = Feed.TubeDepartureBoardsLineStatus;
		List<Result> results = new LinkedList<Result>();

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
		for (Entity entry: entries) {
			if (--max < 0) {
				break; // we've had enough
			}
			Result result = toResult(entry);
			results.add(result);
		}

		List<ResultChange> differences = getDifferences(results, skipErrors);

		// display them
		req.setAttribute("feedChanges", differences);
		req.setAttribute("colors", new TubeStatusPresentationLineColors());
		req.setAttribute("call", new InvokerMap());
		RequestDispatcher view = req.getRequestDispatcher("/LineStatus.jsp");
		view.forward(req, resp);
	}

	private Result toResult(Entity entry) {
		Result result;
		Text content = (Text)entry.getProperty(DSPROP_CONTENT);
		Text error = (Text)entry.getProperty(DSPROP_ERROR);
		Date date = (Date)entry.getProperty(DSPROP_RETRIEVED_DATE);
		if (content != null) {
			try {
				Feed feed = Feed.valueOf(entry.getKind());
				LineStatusFeed feedContents = (LineStatusFeed)feed.getHandler().parse(
						new StringInputStream(content.getValue(), ENCODING));
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
		Query q = new Query(feed.name()).addSort(DSPROP_RETRIEVED_DATE, SortDirection.DESCENDING);
		Iterable<Entity> results = datastore.prepare(q).asIterable();
		return results;
	}

	public static class Result {
		private String errorHeader;
		private String fullError;
		private LineStatusFeed content;
		private Date when;
		public Result(Date when, String error) {
			this.when = when;
			this.errorHeader = error.indexOf('\n') != -1? error.substring(0, error.indexOf('\n')) : error;
			this.fullError = error;
		}
		public Result(Date when, LineStatusFeed content) {
			this.when = when;
			this.content = content;
		}
		public LineStatusFeed getContent() {
			return content;
		}
		public String getErrorHeader() {
			return errorHeader;
		}
		public String getFullError() {
			return fullError;
		}
		public Date getWhen() {
			return when;
		}
	}

	private List<ResultChange> getDifferences(List<Result> results, boolean skipErrors) {
		List<ResultChange> resultChanges = new ArrayList<LineStatusHistoryServlet.ResultChange>(results.size());
		Result newResult = null;
		for (Result oldResult: results) { // we're going forward, but the list is backwards
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
	public static class ResultChange {
		private Result oldResult;
		private Result newResult;
		private String errorChange;
		private Map<Line, String> statusChanges;
		private Map<Line, String> descChanges;
		public ResultChange(Result oldResult, Result newResult) {
			this.oldResult = oldResult;
			this.newResult = newResult;
			statusChanges = new EnumMap<Line, String>(Line.class);
			descChanges = new EnumMap<Line, String>(Line.class);
			diff();
		}
		public Result getOld() {
			return oldResult;
		}
		public Result getNew() {
			return newResult;
		}
		public String getError() {
			return errorChange;
		}
		public Map<Line, String> getStatuses() {
			return statusChanges;
		}
		public Map<Line, String> getDescriptions() {
			return descChanges;
		}

		private void diff() {
			if (oldResult == null && newResult != null) {
				errorChange = "new status";
			} else if (oldResult != null && newResult == null) {
				errorChange = "last status";
			} else if (oldResult != null && newResult != null) {
				diffError();
				diffContent();
			} // else errorChange = null

		}
		protected void diffContent() {
			if (oldResult.getContent() == null || newResult.getContent() == null) {
				return;
			}
			Map<Line, LineStatus> oldMap = oldResult.getContent().getStatusMap();
			Map<Line, LineStatus> newMap = newResult.getContent().getStatusMap();
			Set<Line> allLines = new HashSet<Line>();
			allLines.addAll(oldMap.keySet());
			allLines.addAll(newMap.keySet());
			for (Line line: allLines) {
				LineStatus oldStatus = oldMap.get(line);
				LineStatus newStatus = newMap.get(line);
				if (oldStatus == null || newStatus == null) {
					statusChanges.put(line, "N/A");
					continue;
				}
				int statusDiff = oldStatus.getType().compareTo(newStatus.getType());
				if (statusDiff < 0) {
					statusChanges.put(line, "better");
				} else if (statusDiff > 0) {
					statusChanges.put(line, "worse");
				} else {
					statusChanges.put(line, "same");
					diffDesc(line, oldStatus, newStatus);
				}
			}
		}

		protected void diffDesc(Line line, LineStatus oldStatus, LineStatus newStatus) {
			String oldDesc = oldStatus.getDescription();
			String newDesc = newStatus.getDescription();
			if (oldDesc == null && newDesc != null) {
				statusChanges.put(line, "desc added");
			} else if (oldDesc != null && newDesc == null) {
				statusChanges.put(line, "desc removed");
			} else if (oldDesc != null && newDesc != null) {
				if (oldDesc.equals(newDesc)) {
					statusChanges.put(line, "same");
				} else {
					statusChanges.put(line, "changed");
					descChanges.put(line, diffDesc(oldDesc, newDesc));
				}
			}
		}
		private String diffDesc(String oldDesc, String newDesc) {
			diff_match_patch differ = new diff_match_patch();
			LinkedList<Diff> diff = differ.diff_main(oldDesc, newDesc);
			differ.diff_cleanupSemantic(diff);
			return differ.diff_prettyHtml(diff);
		}
		protected void diffError() {
			String oldErrorHeader = oldResult.getErrorHeader();
			String newErrorHeader = newResult.getErrorHeader();
			if (oldErrorHeader == null && newErrorHeader != null) {
				errorChange = "errored";
			} else if (oldErrorHeader != null && newErrorHeader == null) {
				errorChange = "fixed";
			} else if (oldErrorHeader != null && newErrorHeader != null) {
				errorChange = oldErrorHeader.equals(newErrorHeader)? "same" : "changed";
			} else {
				errorChange = null;
			}
		}
	}
}