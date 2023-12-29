package net.twisterrob.travel.statushistory.controller

import io.micronaut.http.HttpRequest
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import net.twisterrob.blt.data.StaticData
import net.twisterrob.blt.model.LineColors
import net.twisterrob.travel.domain.london.status.api.HistoryUseCase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.startsWith
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * @see LineStatusHistoryController
 */
@MicronautTest
class LineStatusHistoryControllerIntegrationTest {

	@Inject
	lateinit var client: BlockingHttpClient

	@MockBean(HistoryUseCase::class)
	val historyUseCase: HistoryUseCase = mock()

	@MockBean(StaticData::class)
	val staticData: StaticData = mock()

	private val lineColors: LineColors = mock()

	@BeforeEach fun setUp() {
		`when`(staticData.lineColors).thenReturn(lineColors)
	}

	@Test fun testEmptyRequest() {
		val request: HttpRequest<Unit> = HttpRequest.GET("/LineStatusHistory")

		val body = client.retrieve(request)

		assertNotNull(body)
		assertThat(body, startsWith("<!DOCTYPE html>\n<html lang=\"en\">"))
		assertThat(body, endsWith("</html>\n"))
	}
}
