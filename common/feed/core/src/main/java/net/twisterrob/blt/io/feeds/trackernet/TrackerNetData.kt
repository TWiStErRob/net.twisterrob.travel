package net.twisterrob.blt.io.feeds.trackernet

import net.twisterrob.blt.model.Line

class TrackerNetData {

	@Suppress("detekt.CyclomaticComplexMethod")
	fun getTrackerNetCodeOf(line: Line): String =
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

	fun fromAlias(alias: String): Line =
		Line.entries.singleOrNull { alias in getAliasesFor(it) }
			?: Line.unknown

	/**
	 * Possible names this Line can appear as.
	 *  * `Name` attribute of `<Line>` in the Line Status feed.
	 */
	fun getAliasesFor(line: Line): List<String> =
		when (line) {
			Line.Bakerloo -> listOf("Bakerloo")
			Line.Central -> listOf("Central")
			Line.Circle -> listOf("Circle") // Hammersmith & Circle?
			Line.District -> listOf("District")
			Line.HammersmithAndCity -> listOf("Hammersmith & City", "H'smith & City", "Hammersmith and City")
			Line.Jubilee -> listOf("Jubilee")
			Line.Metropolitan -> listOf("Metropolitan")
			Line.Northern -> listOf("Northern")
			Line.Piccadilly -> listOf("Piccadilly")
			Line.Victoria -> listOf("Victoria")
			Line.WaterlooAndCity -> listOf("Waterloo & City", "Waterloo and City")
			Line.DLR -> listOf("DLR", "Docklands Light Railway")
			Line.Overground -> listOf("Overground", "East London")
			Line.Tram -> listOf("Trams", "Tram", "Tramlink 1", "Tramlink 2", "Tramlink 3", "Tramlink 4")
			Line.EmiratesAirline -> listOf("Emirates Air Line", "Emirates Airline")
			Line.TflRail -> listOf("TfL Rail")
			Line.ElizabethLine -> listOf("Elizabeth line", "Elizabeth Line", "Elizabeth")
			Line.unknown -> emptyList()
		}

	fun getDisplayName(line: Line): String =
		when (line) {
			Line.unknown -> "Unknown"
			else -> getAliasesFor(line).first()
		}

	/**
	 * `ID` attribute of `<Line>` in the Line Status feed.
	 */
	fun getLineId(line: Line): Int? =
		when (line) {
			Line.Bakerloo -> 1
			Line.Central -> 2
			Line.Circle -> 7
			Line.District -> 9
			Line.HammersmithAndCity -> 8
			Line.Jubilee -> 4
			Line.Metropolitan -> 11
			Line.Northern -> 5
			Line.Piccadilly -> 6
			Line.Victoria -> 3
			Line.WaterlooAndCity -> 12
			Line.DLR -> 81
			Line.Overground -> 82
			Line.Tram -> 90
			Line.EmiratesAirline -> null
			Line.TflRail -> 83
			Line.ElizabethLine -> 83
			Line.unknown -> null
		}
}
