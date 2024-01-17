package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.annotation.QueryValue
import io.micronaut.views.View
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.api.StatusHistoryUseCase
import net.twisterrob.travel.statushistory.viewmodel.LineStatusModelMapper

@Controller
class LineStatusHistoryController(
	private val useCase: StatusHistoryUseCase,
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
		val changes = useCase.history(Feed.TubeDepartureBoardsLineStatus, max, displayCurrent, displayErrors)
		return HttpResponse.ok(modelMapper.map(changes))
	}
}
