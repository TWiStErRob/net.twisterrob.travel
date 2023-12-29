package net.twisterrob.travel.statushistory.test

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

@Factory
class BlockingHttpClientFactory {

	@Prototype
	fun client(@Client("/") client: HttpClient): BlockingHttpClient = client.toBlocking()
}

fun BlockingHttpClient.assertErrorStatus(status: HttpStatus, request: HttpRequest<*>) {
	val ex = assertThrows<HttpClientResponseException> { retrieve(request) }
	assertEquals(status, ex.status)
}
