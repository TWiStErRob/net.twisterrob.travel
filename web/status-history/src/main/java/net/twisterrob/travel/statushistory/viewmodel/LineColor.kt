package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineColors
import java.util.Locale

class LineColor(
	private val colors: LineColors,
	val line: Line,
) {

	val foregroundColor: String
		get() = line.getForeground(colors).toColorString()

	val backgroundColor: String
		get() = line.getBackground(colors).toColorString()

	class AllColors(
		private val colors: LineColors,
	) : Iterable<LineColor> {

		override fun iterator(): Iterator<LineColor> =
			@OptIn(ExperimentalStdlibApi::class)
			object : Iterator<LineColor> {
				private val lines = Line.entries
				private var current = 0

				override fun hasNext(): Boolean =
					current < lines.size

				override fun next(): LineColor {
					if (!hasNext()) throw NoSuchElementException()
					return LineColor(colors, lines[current++])
				}
			}
	}

	companion object {

		private fun Int.toColorString(): String =
			"#%06X".format(Locale.ROOT, this and @Suppress("detekt.MagicNumber") 0xFFFFFF)
	}
}
