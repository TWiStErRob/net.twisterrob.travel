package net.twisterrob.blt.gapp;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.tools.ant.filters.StringInputStream;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import net.twisterrob.blt.gapp.viewmodel.LineColor;
import net.twisterrob.blt.io.feeds.Feed;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus;
import net.twisterrob.blt.model.Line;
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

	public static class Result {
		private final String errorHeader;
		private final String fullError;
		private final LineStatusFeed content;
		private final Date when;
		public Result(Date when, String error) {
			this.when = when;
			this.content = null;
			this.errorHeader = error.indexOf('\n') != -1? error.substring(0, error.indexOf('\n')) : error;
			this.fullError = error;
		}
		public Result(Date when, LineStatusFeed content) {
			this.when = when;
			this.content = content;
			this.errorHeader = null;
			this.fullError = null;
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
	public static class ResultChange {
		private Result oldResult;
		private Result newResult;
		private ErrorChange errorChange;
		private Map<Line, StatusChange> statusChanges;
		private Map<Line, String> descChanges;
		public ResultChange(Result oldResult, Result newResult) {
			this.oldResult = oldResult;
			this.newResult = newResult;
			statusChanges = new EnumMap<>(Line.class);
			descChanges = new EnumMap<>(Line.class);
			diff();
		}
		public Result getOld() {
			return oldResult;
		}
		public Result getNew() {
			return newResult;
		}
		public ErrorChange getError() {
			return errorChange;
		}
		public Map<Line, StatusChange> getStatuses() {
			return statusChanges;
		}
		public Map<Line, String> getDescriptions() {
			return descChanges;
		}

		private void diff() {
			if (oldResult != null && newResult != null) { // after this check fails one of them must be null
				diffError();
				diffContent();
			} else if (newResult != null) {
				errorChange = ErrorChange.NewStatus;
			} else if (oldResult != null) {
				errorChange = ErrorChange.LastStatus;
			} else {
				errorChange = ErrorChange.NoErrors;
			}
		}

		protected void diffContent() {
			if (oldResult.getContent() == null || newResult.getContent() == null) {
				return;
			}
			Map<Line, LineStatus> oldMap = oldResult.getContent().getStatusMap();
			Map<Line, LineStatus> newMap = newResult.getContent().getStatusMap();
			Set<Line> allLines = new HashSet<>();
			allLines.addAll(oldMap.keySet());
			allLines.addAll(newMap.keySet());
			for (Line line : allLines) {
				LineStatus oldStatus = oldMap.get(line);
				LineStatus newStatus = newMap.get(line);
				if (oldStatus == null || newStatus == null) {
					statusChanges.put(line, StatusChange.Unknown);
					continue;
				}
				int statusDiff = oldStatus.getType().compareTo(newStatus.getType());
				if (statusDiff < 0) {
					statusChanges.put(line, StatusChange.Better);
				} else if (statusDiff > 0) {
					statusChanges.put(line, StatusChange.Worse);
				} else /* (statusDiff == 0) */ {
					statusChanges.put(line, StatusChange.Same);
					diffDesc(line, oldStatus, newStatus);
				}
			}
		}

		protected void diffDesc(Line line, LineStatus oldStatus, LineStatus newStatus) {
			String oldDesc = oldStatus.getDescription();
			String newDesc = newStatus.getDescription();
			if (oldDesc != null && newDesc != null) { // after this check fails one of them must be null
				if (oldDesc.equals(newDesc)) {
					statusChanges.put(line, StatusChange.SameDescriptionSame);
				} else {
					statusChanges.put(line, StatusChange.SameDescriptionChange);
					descChanges.put(line, diffDesc(oldDesc, newDesc));
				}
			} else if (newDesc != null) {
				statusChanges.put(line, StatusChange.SameDescriptionAdd);
			} else if (oldDesc != null) {
				statusChanges.put(line, StatusChange.SameDescriptionDel);
			} else {
				// no op
			}
		}

		private static String diffDesc(String oldDesc, String newDesc) {
			diff_match_patch differ = new diff_match_patch();
			LinkedList<Diff> diff = differ.diff_main(oldDesc, newDesc);
			differ.diff_cleanupSemantic(diff);
			return differ.diff_prettyHtml(diff);
		}

		protected void diffError() {
			String oldErrorHeader = oldResult.getErrorHeader();
			String newErrorHeader = newResult.getErrorHeader();
			if (oldErrorHeader != null && newErrorHeader != null) { // after this check fails one of them must be null
				errorChange = oldErrorHeader.equals(newErrorHeader)? ErrorChange.Same : ErrorChange.Change;
			} else if (newErrorHeader != null) {
				errorChange = ErrorChange.Failed;
			} else if (oldErrorHeader != null) {
				errorChange = ErrorChange.Fixed;
			} else {
				errorChange = ErrorChange.NoErrors;
			}
		}

		public enum StatusChange {
			Better("better", "status-better"),
			Worse("worse", "status-worse"),
			Same("", "status-same"),
			Unknown("unknown", "status-unknown"),
			SameDescriptionSame("", "status-same-desc-same"),
			SameDescriptionChange("description", "status-same-desc-change"),
			SameDescriptionAdd("description", "status-same-desc-add"),
			SameDescriptionDel("description", "status-same-desc-del");
			private final String title;
			private final String cssClass;
			StatusChange(String humanReadable, String cssClass) {
				this.title = humanReadable;
				this.cssClass = cssClass;
			}
			public String getTitle() {
				return title;
			}
			public String getCssClass() {
				return cssClass;
			}
		}

		public enum ErrorChange {
			Same("same error"),
			Change("error changed"),
			Failed("new error"),
			Fixed("error fixed"),
			NoErrors(""),
			NewStatus(""),
			LastStatus("");
			private final String title;
			ErrorChange(String humanReadable) {
				title = humanReadable;
			}
			public String getTitle() {
				return title;
			}
		}
	}
}
