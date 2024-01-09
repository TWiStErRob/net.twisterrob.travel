package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.io.feeds.trackernet.LineStatusFeed
import java.util.Date

sealed interface Result {

	class ContentResult(
		val `when`: Date,
		val content: LineStatusFeed,
	) : Result

	class ErrorResult(
		val `when`: Date,
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
