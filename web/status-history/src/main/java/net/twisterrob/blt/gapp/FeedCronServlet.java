package net.twisterrob.blt.gapp;

import java.io.*;
import java.util.*;

import javax.annotation.Nullable;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import jakarta.servlet.http.*;

import org.slf4j.*;

import com.google.cloud.datastore.*;

import net.twisterrob.travel.domain.london.status.DomainStatusHistoryUseCase;
import net.twisterrob.travel.domain.london.status.Feed;
import net.twisterrob.travel.domain.london.status.StatusItem;
import net.twisterrob.travel.domain.london.status.api.RefreshResult;
import net.twisterrob.travel.domain.london.status.api.StatusHistoryUseCase;

@Controller
@SuppressWarnings("serial")
public class FeedCronServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(FeedCronServlet.class);

	private static final String QUERY_FEED = "feed";

	private final StatusHistoryUseCase useCase = new DomainStatusHistoryUseCase(
			new DatastoreStatusHistoryRepository(DatastoreOptions.getDefaultInstance().getService()),
			new HttpStatusInteractor()
	);

	@Get("/FeedCron")
	@Override public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String feedString = req.getParameter(QUERY_FEED);
		Feed feed = parseFeed(feedString);
		if (feed == null) {
			String message = String.format(Locale.getDefault(), "No such feed: '%s'.", feedString);
			LOG.warn(message);
			resp.getWriter().println(message);
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		RefreshResult result = useCase.refreshLatest(feed);
		Marker marker = MarkerFactory.getMarker(feed.name());
		if (result instanceof RefreshResult.NoChange noChange) {
			if (noChange.getLatest() instanceof StatusItem.SuccessfulStatusItem) {
				LOG.info(marker, "They have the same content.");
				resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				LOG.info(marker, "They have the same error.");
				resp.setStatus(HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION);
			}
		} else if (result instanceof RefreshResult.Created) {
			LOG.info(marker, "It's new, stored...");
			resp.setStatus(HttpServletResponse.SC_CREATED);
		} else if (result instanceof RefreshResult.Refreshed) {
			LOG.info(marker, "They're different, stored...");
			resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		} else {
			throw new IllegalStateException("Unknown result: " + result);
		}
	}

	static @Nullable Feed parseFeed(@Nullable String feedString) {
		if (feedString == null) {
			return null;
		}
		try {
			return Feed.valueOf(feedString);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}
}
