package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.server.types.files.StreamedFile
import io.micronaut.views.View
import net.twisterrob.travel.statushistory.viewmodel.Versions

@Controller
class IndexController {

	@Get("/")
	@View("index")
	fun index(): MutableHttpResponse<*> =
		HttpResponse.ok(
			IndexModel(Versions())
		)

	@Get("/favicon.ico")
	fun favicon(): StreamedFile =
		StreamedFile(
			IndexController::class.java.classLoader.getResourceAsStream("public/favicon.ico"),
			MediaType.IMAGE_PNG_TYPE
		)

	@Suppress("unused") // Used by index.hbs.
	private class IndexModel(
		val versions: Versions,
	)
}
