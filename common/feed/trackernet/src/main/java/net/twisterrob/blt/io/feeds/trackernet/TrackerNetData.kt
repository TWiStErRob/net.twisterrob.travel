package net.twisterrob.blt.io.feeds.trackernet

import net.twisterrob.blt.model.Line

class TrackerNetData {

	@Suppress("detekt.CyclomaticComplexMethod")
	fun getTrackerNetCodeOf(line: Line): String =
		@Suppress("DEPRECATION")
		when (line) {
			Line.Bakerloo -> "B"
			Line.Central -> "C"
			Line.Circle -> "H"
			Line.District -> "D"
			Line.HammersmithAndCity -> "H"
			Line.Jubilee -> "J"
			Line.Metropolitan -> "M"
			Line.Northern -> "N"
			Line.Piccadilly -> "P"
			Line.Victoria -> "V"
			Line.WaterlooAndCity -> "W"
			Line.DLR -> "L"
			Line.Overground -> "O"
			Line.Tram -> "T"
			Line.EmiratesAirline -> "E"
			Line.TflRail -> "?"
			Line.ElizabethLine -> "?"
			Line.unknown -> "?"
		}

	fun lineFromTrackerNetCode(code: Char): Line =
		Line.entries.singleOrNull { getTrackerNetCodeOf(it) === code.toString() }
			?: Line.unknown
}
