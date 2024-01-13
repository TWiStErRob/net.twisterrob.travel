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

		val header: String
			get() = error.header
		
		val fullError: String
			get() = error.error

		@JvmInline
		value class Error(
			val error: String,
		) {

			val header: String
				get() = error.substringBefore('\n')
		}
	}
}
