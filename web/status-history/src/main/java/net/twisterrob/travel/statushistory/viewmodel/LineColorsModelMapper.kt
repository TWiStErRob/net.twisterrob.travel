package net.twisterrob.travel.statushistory.viewmodel

import jakarta.inject.Inject
import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineColors

class LineColorsModelMapper @Inject constructor(
	private val colors: LineColors,
) {

	fun map(): List<LineColorsModel> =
		Line.entries.map { line ->
			LineColorsModel(
				lineId = line.name,
				foregroundColor = colors.getForeground(line),
				backgroundColor = colors.getBackground(line),
			)
		}
}
