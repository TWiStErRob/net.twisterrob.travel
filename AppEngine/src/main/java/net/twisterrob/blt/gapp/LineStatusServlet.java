package net.twisterrob.blt.gapp;
import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.blt.model.TubeStatusPresentationLineColors;
import net.twisterrob.java.web.InvokerMap;

import org.slf4j.*;
import org.xml.sax.SAXException;

@SuppressWarnings("serial")
public class LineStatusServlet extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(LineStatusServlet.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOG.info(LineStatusServlet.class + " called");
		try {
			LineStatusFeed feed = downloadFeed(Feed.TubeDepartureBoardsLineStatus);
			req.setAttribute("feed", feed);
		} catch (IOException ex) {
			throw new ServletException("Cannot load feed", ex);
		} catch (SAXException ex) {
			throw new ServletException("Cannot load feed", ex);
		}
		req.setAttribute("colors", new TubeStatusPresentationLineColors());

		req.setAttribute("call", new InvokerMap());
		RequestDispatcher view = req.getRequestDispatcher("/LineStatus.jsp");
		view.forward(req, resp);
	}

	private <T extends BaseFeed> T downloadFeed(Feed feed) throws IOException, SAXException {
		URL url = new LocalhostUrlBuilder().getFeedUrl(feed);
		LOG.debug("%s", url);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.connect();
		InputStream input = connection.getInputStream();

		@SuppressWarnings("unchecked")
		T root = (T)feed.getHandler().parse(input);
		return root;
	}
}
