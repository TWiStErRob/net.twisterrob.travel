package net.twisterrob.travel.statushistory.controller;

import java.util.*;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.views.View;

import net.twisterrob.blt.data.StaticData;
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed;
import net.twisterrob.travel.domain.london.status.Feed;
import net.twisterrob.travel.domain.london.status.api.HistoryUseCase;
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem;
import net.twisterrob.travel.statushistory.viewmodel.LineColor;
import net.twisterrob.travel.statushistory.viewmodel.Result;
import net.twisterrob.travel.statushistory.viewmodel.ResultChange;

@Controller
public class LineStatusHistoryController {

	private final HistoryUseCase useCase;
	private final StaticData staticData;

	public LineStatusHistoryController(HistoryUseCase useCase, StaticData staticData) {
		this.useCase = useCase;
		this.staticData = staticData;
	}

	@Get("/LineStatusHistory")
	@View("LineStatus")
	@Produces(MediaType.TEXT_HTML)
	public HttpResponse<?> lineStatusHistory(
			@QueryValue(value = "current", defaultValue = "false") boolean displayCurrent,
			@QueryValue(value = "errors", defaultValue = "false") boolean displayErrors,
			@QueryValue(value = "max", defaultValue = "100") int max
	) {
		Feed feed = Feed.TubeDepartureBoardsLineStatus;

		List<ParsedStatusItem> history = useCase.history(feed, max, displayCurrent);
		List<Result> results = history
				.stream()
				.filter((it) -> displayErrors || !(it instanceof ParsedStatusItem.ParseFailed))
				.map(LineStatusHistoryController::toResult).toList();
		List<ResultChange> differences = getDifferences(results);

		return HttpResponse.ok(
				new LineStatusHistoryModel(
						differences,
						new LineColor.AllColors(staticData.getLineColors())
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
