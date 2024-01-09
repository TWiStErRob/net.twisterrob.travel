package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import io.micronaut.views.View
import net.twisterrob.blt.data.StaticData
import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem
import net.twisterrob.travel.statushistory.viewmodel.LineColor
import net.twisterrob.travel.statushistory.viewmodel.Result
import net.twisterrob.travel.statushistory.viewmodel.ResultChange
import net.twisterrob.travel.statushistory.viewmodel.ResultChangeCalculator
import net.twisterrob.travel.statushistory.viewmodel.ResultChangeModel
import net.twisterrob.travel.statushistory.viewmodel.ResultChangeModelMapper
import java.util.Date

@Controller
class LineStatusHistoryController(
	private val useCase: StatusHistoryRepository,
	private val staticData: StaticData,
) {

	@Get("/LineStatusHistory")
	@View("LineStatus")
	@Produces(MediaType.TEXT_HTML)
	fun lineStatusHistory(
		@QueryValue(value = "current", defaultValue = "false") displayCurrent: Boolean,
		@QueryValue(value = "errors", defaultValue = "false") displayErrors: Boolean,
		@QueryValue(value = "max", defaultValue = "100") max: Int,
	): HttpResponse<*> {
		val feed = Feed.TubeDepartureBoardsLineStatus
		val history = useCase.history(feed, max, displayCurrent)
		val results = history
			.filter { displayErrors || it !is ParsedStatusItem.ParseFailed }
			.map(ParsedStatusItem::toResult)
		val differences = getDifferences(results)

		return HttpResponse.ok(
			LineStatusHistoryModel(
				differences.map(ResultChangeModelMapper()::map),
				LineColor.AllColors(staticData.lineColors)
			)
		)
	}

	@Suppress("unused") // Used by LineStatus.hbs.
	private class LineStatusHistoryModel(
		val feedChanges: List<ResultChangeModel>,
		val colors: Iterable<LineColor>,
	)
}

private fun ParsedStatusItem.toResult(): Result {
	val date = Date(this.item.retrievedDate.toEpochMilliseconds())
	return when (this) {
		is ParsedStatusItem.ParsedFeed ->
			Result.ContentResult(date, this.content as LineStatusFeed)

		is ParsedStatusItem.AlreadyFailed ->
			Result.ErrorResult(date, Result.ErrorResult.Error(this.item.error.stacktrace))

		is ParsedStatusItem.ParseFailed ->
			Result.ErrorResult(date, Result.ErrorResult.Error("Error while displaying loaded XML: ${this.error.stacktrace}"))
	}
}

private fun getDifferences(results: List<Result>): List<ResultChange> {
	val resultChanges: MutableList<ResultChange> = ArrayList(results.size)
	var newResult: Result? = null
	for (oldResult in results) { // We're going forward, but the list is backwards.
		resultChanges.add(ResultChangeCalculator().diff(oldResult, newResult))
		newResult = oldResult
	}
	resultChanges.add(ResultChangeCalculator().diff(null, newResult))
	resultChanges.removeAt(0)
	return resultChanges
}
