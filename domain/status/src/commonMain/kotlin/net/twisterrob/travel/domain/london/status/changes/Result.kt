package net.twisterrob.travel.domain.london.status.changes

import net.twisterrob.blt.model.LineStatuses
import kotlinx.datetime.Instant

sealed interface Result {

	@Suppress("detekt.VariableNaming")
	val `when`: Instant

	class ContentResult(
		override val `when`: Instant,
		val content: LineStatuses,
	) : Result

	class ErrorResult(
		override val `when`: Instant,
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
