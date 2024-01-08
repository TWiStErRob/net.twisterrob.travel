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
		val fullError: String,
		val errorHeader: String? = fullError.substringBefore('\n'),
	) : Result
}
