package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.model.LineStatuses
import java.util.Date

sealed interface Result {

	@Suppress("detekt.VariableNaming")
	val `when`: Date

	class ContentResult(
		override val `when`: Date,
		val content: LineStatuses,
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
