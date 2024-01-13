package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import java.util.Date

sealed interface Result {

	// Used by Handlebars reflection in LineStatus.hbs.
	@Suppress("detekt.VariableNaming")
	val `when`: Date

	class ContentResult(
		override val `when`: Date,
		val content: LineStatusFeed,
	) : Result

	class ErrorResult(
		override val `when`: Date,
		val error: Error,
	) : Result {

		@JvmInline
		value class Error(
			val text: String,
		) {

			val header: String
				get() = text.substringBefore('\n')
		}
	}
}
