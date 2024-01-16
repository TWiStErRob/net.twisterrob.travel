package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import io.micronaut.views.View
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.statushistory.viewmodel.LineStatusModelMapper
import net.twisterrob.travel.statushistory.viewmodel.Result
import net.twisterrob.travel.statushistory.viewmodel.ResultChangesCalculator
import java.util.Date

@Controller
class LineStatusHistoryController(
	private val useCase: StatusHistoryRepository,
	private val modelMapper: LineStatusModelMapper,
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
		val changes = ResultChangesCalculator().getChanges(results)

		return HttpResponse.ok(modelMapper.map(changes))
	}
}

private fun ParsedStatusItem.toResult(): Result {
	val date = Date(this.item.retrievedDate.toEpochMilliseconds())
	return when (this) {
		is ParsedStatusItem.ParsedFeed ->
			Result.ContentResult(date, this.content)

		is ParsedStatusItem.AlreadyFailed ->
			Result.ErrorResult(date, Result.ErrorResult.Error(this.item.error.stacktrace))

		is ParsedStatusItem.ParseFailed ->
			Result.ErrorResult(date, Result.ErrorResult.Error("Error while displaying loaded XML: ${this.error.stacktrace}"))
	}
}
