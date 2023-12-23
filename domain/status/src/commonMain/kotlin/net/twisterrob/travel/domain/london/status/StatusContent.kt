package net.twisterrob.travel.domain.london.status

class StatusContent(
	val content: String,
) {

	override fun equals(other: Any?): Boolean =
		other is StatusContent && other.content == this.content
}
