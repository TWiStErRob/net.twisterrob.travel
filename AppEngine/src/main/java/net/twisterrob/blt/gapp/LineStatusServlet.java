package net.twisterrob.blt.gapp;
import static net.twisterrob.blt.gapp.LineStatusConsts.*;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.TubeStatusPresentationLineColors;
import net.twisterrob.java.io.IOTools;
import net.twisterrob.java.utils.ObjectTools;
import net.twisterrob.java.web.InvokerMap;

import org.apache.tools.ant.filters.StringInputStream;
import org.slf4j.*;
import org.xml.sax.SAXException;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.SortDirection;

@SuppressWarnings("serial")
public class LineStatusServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(LineStatusServlet.class);

	private static final String QUERY_DISPLAY_CURRENT = "current";
	private static final String QUERY_DISPLAY_MAX = "max";
	private static final int DISPLAY_MAX_DEFAULT = 100;

	private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Feed feed = Feed.TubeDepartureBoardsLineStatus;
		List<Result> results = new LinkedList<Result>();

		int max;
		try {
			max = Integer.parseInt(req.getParameter(QUERY_DISPLAY_MAX));
		} catch (NumberFormatException ex) {
			max = DISPLAY_MAX_DEFAULT;
		}
		if (Boolean.parseBoolean(req.getParameter(QUERY_DISPLAY_CURRENT))) {
			results.add(getCurrent(feed));
		}

		Query q = new Query(feed.name()).addSort(DSPROP_RETRIEVED_DATE, SortDirection.DESCENDING);
		for (Entity result: datastore.prepare(q).asIterable()) {
			if (--max < 0) {
				break; // we've had enough
			}
			Text content = (Text)result.getProperty(DSPROP_CONTENT);
			Text error = (Text)result.getProperty(DSPROP_ERROR);
			Date date = (Date)result.getProperty(DSPROP_RETRIEVED_DATE);
			if (content != null) {
				try {
					BaseFeed feedContents = feed.getHandler()
							.parse(new StringInputStream(content.getValue(), ENCODING));
					results.add(new Result(date, feedContents));
				} catch (Exception ex) {
					results.add(new Result(date, "Error while displaying loaded XML: "
							+ ObjectTools.getFullStackTrace(ex)));
				}
			} else if (error != null) {
				results.add(new Result(date, error.getValue()));
			}
		}

		req.setAttribute("feeds", results);
		req.setAttribute("colors", new TubeStatusPresentationLineColors());
		req.setAttribute("call", new InvokerMap());
		RequestDispatcher view = req.getRequestDispatcher("/LineStatus.jsp");
		view.forward(req, resp);
	}
	protected Result getCurrent(Feed feed) throws ServletException, IOException {
		try {
			LineStatusFeed feedContents = downloadFeed(feed);
			return new Result(new Date(), feedContents);
		} catch (Exception ex) {
			return new Result(new Date(), ObjectTools.getFullStackTrace(ex));
		}
	}

	private <T extends BaseFeed> T downloadFeed(Feed feed) throws IOException, SAXException {
		URL url = URL_BUILDER.getFeedUrl(feed);
		LOG.debug(url.toString());
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.connect();
		InputStream input = null;
		try {
			input = connection.getInputStream();
		} finally {
			IOTools.ignorantClose(input);
		}

		@SuppressWarnings("unchecked")
		T root = (T)feed.getHandler().parse(input);
		return root;
	}

	public static class Result {
		private String errorHeader;
		private String fullError;
		private BaseFeed content;
		private Date when;
		public Result(Date when, String error) {
			this.when = when;
			this.errorHeader = error.indexOf('\n') != -1? error.substring(0, error.indexOf('\n')) : error;
			this.fullError = error;
		}
		public Result(Date when, BaseFeed content) {
			this.when = when;
			this.content = content;
		}
		public BaseFeed getContent() {
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
}
