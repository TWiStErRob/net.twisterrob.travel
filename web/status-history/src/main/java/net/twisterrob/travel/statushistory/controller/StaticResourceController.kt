package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.server.types.files.StreamedFile
import java.io.InputStream

/**
 * Controller to serve static JavaScript files.
 * 
 * This works around the InvalidPathException that occurs in Java 21 when Jetty's
 * static resource handler tries to resolve paths in ZIP filesystems (JAR files).
 * By using getResourceAsStream directly, we avoid the Path.of() call that fails
 * with absolute paths in ZIP filesystems.
 */
@Controller("/static")
class StaticResourceController {

	@Get("/jquery-1.10.2.min.js")
	fun jqueryJs(): StreamedFile = serveStaticFile("public/static/jquery-1.10.2.min.js", MediaType.TEXT_JAVASCRIPT_TYPE)

	@Get("/htmltooltip.js")
	fun htmlTooltipJs(): StreamedFile = serveStaticFile("public/static/htmltooltip.js", MediaType.TEXT_JAVASCRIPT_TYPE)

	@Get("/jquery-1.2.2.pack.js")
	fun jqueryOldJs(): StreamedFile = serveStaticFile("public/static/jquery-1.2.2.pack.js", MediaType.TEXT_JAVASCRIPT_TYPE)

	@Get("/jquery.min.map")
	fun jqueryMap(): StreamedFile = serveStaticFile("public/static/jquery.min.map", MediaType.APPLICATION_JSON_TYPE)

	private fun serveStaticFile(resourcePath: String, mediaType: MediaType): StreamedFile {
		val inputStream: InputStream = StaticResourceController::class.java.classLoader.getResourceAsStream(resourcePath)
			?: throw IllegalArgumentException("Resource not found: $resourcePath")
		return StreamedFile(inputStream, mediaType)
	}
}
