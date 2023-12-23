package net.twisterrob.travel.domain.london.status

class Stacktrace(
	val stacktrace: String,
) {

	override fun equals(other: Any?): Boolean =
		other is Stacktrace && other.stacktrace == this.stacktrace
}
