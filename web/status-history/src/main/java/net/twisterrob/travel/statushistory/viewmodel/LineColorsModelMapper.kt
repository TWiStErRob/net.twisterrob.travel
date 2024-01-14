package net.twisterrob.travel.statushistory.viewmodel

import jakarta.inject.Inject
import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineColors

class LineColorsModelMapper @Inject constructor(
	private val colors: LineColors
) {

	@OptIn(ExperimentalStdlibApi::class)
	fun map(): List<LineColorsModel> =
		Line.entries.map { line ->
			LineColorsModel(
				line = line,
				foregroundColor = colors.getForeground(line),
				backgroundColor = colors.getBackground(line),
			)
		}
}