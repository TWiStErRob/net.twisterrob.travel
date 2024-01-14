package net.twisterrob.travel.statushistory.viewmodel

import jakarta.inject.Inject
import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineColorer

class LineColorsModelMapper @Inject constructor(
	private val lineColorer: LineColorer
) {

	@OptIn(ExperimentalStdlibApi::class)
	fun map(): List<LineColorsModel> =
		Line.entries.map { line ->
			LineColorsModel(
				line = line,
				foregroundColor = lineColorer.getForeground(line),
				backgroundColor = lineColorer.getBackground(line),
			)
		}
}
