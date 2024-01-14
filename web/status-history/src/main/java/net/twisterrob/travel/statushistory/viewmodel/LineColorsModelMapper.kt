package net.twisterrob.travel.statushistory.viewmodel

import jakarta.inject.Inject
import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineColors
import java.util.Locale

class LineColorsModelMapper @Inject constructor(
	private val colors: LineColors,
) {

	@OptIn(ExperimentalStdlibApi::class)
	fun map(): List<LineColorsModel> =
		Line.entries.map { line ->
			LineColorsModel(
				line = line,
				foregroundColor = line.getForeground(colors).toColorString(),
				backgroundColor = line.getBackground(colors).toColorString(),
			)
		}

	companion object {

		private fun Int.toColorString(): String =
			"#%06X".format(Locale.ROOT, this and @Suppress("detekt.MagicNumber") 0xFFFFFF)
	}
}
