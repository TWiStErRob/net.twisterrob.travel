package net.twisterrob.travel.domain.london.status

import javax.sound.sampled.Line

class LineStatuses(
	val statuses: Map<Line, LineStatus>,
) {
}

class LineStatus(
	val line: Line,
) {
}
