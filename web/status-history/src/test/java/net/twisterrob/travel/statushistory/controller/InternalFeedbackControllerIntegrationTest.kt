package net.twisterrob.travel.statushistory.controller

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import net.twisterrob.travel.statushistory.infrastructure.github.contract.GithubApiClient
import net.twisterrob.travel.statushistory.infrastructure.secrets.Variables
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * @see InternalFeedbackController
 */
@MicronautTest
class InternalFeedbackControllerIntegrationTest {

	/**
	 * @see InternalFeedbackController.doGet
	 */
	@Test
	fun `creates issue when does not exists`() {
		ApplicationContext.run(
			EmbeddedServer::class.java,
			mapOf(
				"test.class" to "InternalFeedbackControllerIntegrationTest_Service",
				"micronaut.codec.json.additional-types" to listOf("application/vnd.github.v3+json"),
			),
		).use { github ->
			ApplicationContext.run(
				EmbeddedServer::class.java,
				mapOf(
					"test.class" to "InternalFeedbackControllerIntegrationTest",
					"micronaut.environment" to "test",
					"micronaut.http.services.github.url" to "http://localhost:${github.port}",
					"micronaut.http.services.github.feedback-repository.owner" to "test-owner",
					"micronaut.http.services.github.feedback-repository.repo" to "test-repo",
				),
			).use { embeddedServer ->
				embeddedServer.applicationContext
					.createBean(HttpClient::class.java, embeddedServer.url).use { httpClient ->
						val client = httpClient.toBlocking()
						client.exchange<Unit>("/test")
					}
			}
		}
	}

	@Factory
	@Suppress("detekt.UnusedPrivateClass") // Used by Micronaut DI.
	private class TestBeans {

		private object StubVariables : Variables {
			override val githubActor: String
				get() = "test-actor"
			override val githubPat: String
				get() = "test-pat"
		}

		@Requires(property = "test.class", value = "InternalFeedbackControllerIntegrationTest")
		@Bean
		fun variablesForProduction(): Variables =
			StubVariables

		@Requires(property = "test.class", value = "InternalFeedbackControllerIntegrationTest_Service")
		@Bean
		fun variablesForGitHubStub(): Variables =
			StubVariables
	}
}

@Requires(property = "test.class", value = "InternalFeedbackControllerIntegrationTest_Service")
@Controller
@Suppress("detekt.UnusedPrivateClass") // Used by Micronaut DI.
private class GithubStub(
	private val variables: Variables,
	// private val resourceLoader: ResourceLoader,
	// resourceLoader.getResource("stub-response.json").map { it.readText() }
) {

	/**
	 * @see GithubApiClient.issuesWithTitle
	 */
	@Get(
		uri = "/search/issues",
		produces = ["application/vnd.github.v3+json"],
	)
	@Language("json")
	fun issueSearchStub(
		@QueryValue("q") q: String,
		@QueryValue("sort") sort: String,
		@QueryValue("order") order: String,
		@Header("Authorization") auth: String,
	): String {
		assertEquals("repo:test-owner/test-repo type:issue state:open in:title \"Hello\"", q)
		assertEquals("created", sort)
		assertEquals("desc", order)
		assertAuth(auth)
		return """
			{
			  "total_count": 0,
			  "incomplete_results": false,
			  "items": []
			}
		""".trimIndent()
	}

	/**
	 * @see GithubApiClient.createIssue
	 */
	@Post(
		uri = "/repos/test-owner/test-repo/issues",
		produces = ["application/vnd.github.v3+json"],
	)
	@Language("json")
	fun createIssueStub(
		@Body("title") title: String,
		@Body("body") body: String,
		@Header("Authorization") auth: String,
	): String {
		assertEquals("Hello", title)
		assertEquals("World", body)
		assertAuth(auth)
		return """
			{
			  "html_url": "https://github.com/test-owner/test-repo/issues/1",
			  "number": 1,
			  "title": "title"
			}
		""".trimIndent()
	}

	private fun assertAuth(auth: String) {
		val userPass = "${variables.githubActor}:${variables.githubPat}"

		@OptIn(ExperimentalEncodingApi::class)
		val encoded = Base64.encode(userPass.encodeToByteArray())
		assertEquals("Basic ${encoded}", auth)
	}
}
