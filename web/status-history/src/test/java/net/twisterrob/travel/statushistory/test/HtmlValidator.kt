package net.twisterrob.travel.statushistory.test

import org.junit.Assert.assertEquals
import org.w3c.tidy.Tidy
import org.w3c.tidy.TidyMessage
import java.io.Writer

object HtmlValidator {

	fun assertValidHtml(html: String) {
		val tidy = Tidy()
		tidy.quiet = true
		tidy.showWarnings = true

		val messages: MutableList<TidyMessage> = mutableListOf()
		tidy.setMessageListener(messages::add)
		tidy.parse(html.reader(), null as Writer?)
		assertEquals("${html}\n${messages}", 0, messages.size)
	}
}
