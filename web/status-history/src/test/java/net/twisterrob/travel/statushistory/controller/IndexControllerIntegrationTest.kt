package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import net.twisterrob.travel.statushistory.test.HtmlValidator.assertValidHtml
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * @see IndexController
 */
@MicronautTest
class IndexControllerIntegrationTest {

	@Inject
	lateinit var client: BlockingHttpClient

	@Test fun testIndex() {
		val request: HttpRequest<Unit> = HttpRequest.GET("/")

		val body = client.retrieve(request)

		assertNotNull(body)
		assertThat(body, containsString("Better London Travel"))
		assertValidHtml(body)
	}

	@Test fun testFavicon() {
		val request: HttpRequest<Unit> = HttpRequest.GET("/favicon.ico")

		val body = client.retrieve(request, ByteArray::class.java)

		assertNotNull(body)
		val expected = IndexController::class.java.getResourceAsStream("/public/favicon.ico")?.use { it.readBytes() }
		assertArrayEquals(expected, body)
	}
}
