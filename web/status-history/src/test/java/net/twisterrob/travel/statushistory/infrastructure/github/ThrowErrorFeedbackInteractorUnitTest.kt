package net.twisterrob.travel.statushistory.infrastructure.github

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ThrowErrorFeedbackInteractorUnitTest {
	private val subject = ThrowErrorFeedbackInteractor()

	@Test
	fun `report throws error`() {
		val title = "title"
		val body = "body"

		val ex = assertThrows<Throwable> {
			subject.report(title, body)
		}

		assertEquals("${title}\n${body}", ex.message)
	}
}
