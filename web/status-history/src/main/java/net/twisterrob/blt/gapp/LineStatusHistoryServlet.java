package net.twisterrob.blt.gapp;

import java.util.*;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;
import jakarta.servlet.http.*;

import com.google.cloud.datastore.DatastoreOptions;

import net.twisterrob.blt.gapp.viewmodel.*;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;
import net.twisterrob.travel.domain.london.status.DomainHistoryUseCase;
import net.twisterrob.travel.domain.london.status.Feed;
import net.twisterrob.travel.domain.london.status.api.HistoryUseCase;
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem;

@Controller
public class LineStatusHistoryServlet {
	private static final String QUERY_DISPLAY_CURRENT = "current";
	private static final String QUERY_DISPLAY_ERRORS = "errors";
	private static final String QUERY_DISPLAY_MAX = "max";
	private static final int DISPLAY_MAX_DEFAULT = 100;

	private final HistoryUseCase useCase = new DomainHistoryUseCase(
			new DatastoreStatusHistoryRepository(DatastoreOptions.getDefaultInstance().getService()),
			new HttpStatusInteractor(),
			new FeedHandlerFeedParser()
	);

	@Get("/LineStatusHistory")
	@View("LineStatus")
	@Produces(MediaType.TEXT_HTML)
	public HttpResponse<?> doGet(HttpServletRequest req, HttpServletResponse resp) {
		List<Result> results = new LinkedList<>();

		// Params
		int max;
		try {
			max = Integer.parseInt(req.getParameter(QUERY_DISPLAY_MAX));
		} catch (NumberFormatException ex) {
			max = DISPLAY_MAX_DEFAULT;
		}
		boolean current = Boolean.parseBoolean(req.getParameter(QUERY_DISPLAY_CURRENT));
		boolean skipErrors = true;
		if (Boolean.parseBoolean(req.getParameter(QUERY_DISPLAY_ERRORS))) {
			skipErrors = false;
		}

		List<ParsedStatusItem> history = useCase.history(Feed.TubeDepartureBoardsLineStatus, max, current, skipErrors);
		history.iterator().forEachRemaining(item -> results.add(toResult(item)));

		List<ResultChange> differences = getDifferences(results);

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

	private static Result toResult(ParsedStatusItem parsed) {
		Result result;
		Date date = new Date(parsed.getItem().getRetrievedDate().toEpochMilliseconds());
		if (parsed instanceof ParsedStatusItem.ParsedFeed feed) {
			result = new Result(date, (LineStatusFeed)feed.getContent());
		} else if (parsed instanceof ParsedStatusItem.AlreadyFailed failure) {
			result = new Result(date, failure.getItem().getError().getStacktrace());
		} else if (parsed instanceof ParsedStatusItem.ParseFailed parseFailure) {
			result = new Result(date, "Error while displaying loaded XML: " + parseFailure.getError().getStacktrace());
		} else {
			throw new IllegalArgumentException("Unsupported parse result: " + parsed);
		}
		return result;
	}

	private static List<ResultChange> getDifferences(List<Result> results) {
		List<ResultChange> resultChanges = new ArrayList<>(results.size());
		Result newResult = null;
		for (Result oldResult : results) { // we're going forward, but the list is backwards
			resultChanges.add(new ResultChange(oldResult, newResult));
			newResult = oldResult;
		}
		resultChanges.add(new ResultChange(null, newResult));
		resultChanges.remove(0);
		return resultChanges;
	}
}
