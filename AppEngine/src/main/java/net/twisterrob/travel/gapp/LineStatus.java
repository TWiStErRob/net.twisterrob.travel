package net.twisterrob.travel.gapp;
import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import org.slf4j.*;

@SuppressWarnings("serial")
public class LineStatus extends HttpServlet {
	private static final Logger LOG = LoggerFactory.getLogger(LineStatus.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		RequestDispatcher view = req.getRequestDispatcher("/LineStatus.jsp");
		view.forward(req, resp);
	}
}
