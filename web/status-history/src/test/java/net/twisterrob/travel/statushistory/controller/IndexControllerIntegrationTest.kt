package net.twisterrob.travel.statushistory.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import net.twisterrob.travel.statushistory.test.HtmlValidator.assertValidHtml
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.AfterClass
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNotNull
import org.junit.BeforeClass
import org.junit.Test

// TODO use @MicronautTest in JUnit 5.
class IndexControllerIntegrationTest {

	@Test fun testIndex() {
		val request: HttpRequest<Unit> = HttpRequest.GET("/")

		val body = client.toBlocking().retrieve(request)

		assertNotNull(body)
		assertThat(body, containsString("Better London Travel"))
		assertValidHtml(body)
	}

	@Test fun testFavicon() {
		val request: HttpRequest<Unit> = HttpRequest.GET("/favicon.ico")

		val body = client.toBlocking().retrieve(request, ByteArray::class.java)

		assertNotNull(body)
		val expected = IndexController::class.java.getResourceAsStream("/public/favicon.ico")?.use { it.readBytes() }
		assertArrayEquals(expected, body)
	}

	companion object {

		private lateinit var server: EmbeddedServer
		private lateinit var client: HttpClient

		@JvmStatic
		@BeforeClass fun setupServer() {
			server = ApplicationContext
				.run(EmbeddedServer::class.java)
			client = server
				.applicationContext
				.createBean(HttpClient::class.java, server.url)
		}

		@JvmStatic
		@AfterClass fun stopServer() {
			if (this::server.isInitialized) {
				server.stop()
			}
			if (this::client.isInitialized) {
				client.stop()
			}
		}
	}
}
