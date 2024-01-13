package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import java.util.Date

sealed interface Result {

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
			val error: String,
		) {

			val header: String
				get() = error.substringBefore('\n')
		}
	}
}
