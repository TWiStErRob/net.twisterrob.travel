package net.twisterrob.blt.io.feeds.trackernet

import net.twisterrob.blt.model.Line

/**
 * Data for possible lines that may appear in line status, for example:
 * ```
 * <Line ID="8" Name="Hammersmith and City" />
 * ```
 *
 * See [Current status API](http://cloud.tfl.gov.uk/TrackerNet/LineStatus).
 * @see net.twisterrob.blt.io.feeds.Feed.TubeDepartureBoardsLineStatus
 * @see net.twisterrob.blt.io.feeds.Feed.TubeDepartureBoardsLineStatusIncidents
 */
class TrackerNetData {

	@Suppress("detekt.CyclomaticComplexMethod")
	fun getTrackerNetCodeOf(line: Line): String =
		when (line) {
			Line.Bakerloo -> "B"
			Line.Central -> "C"
			Line.Circle -> "H"
			Line.District -> "D"
			// H from http://www.tfl.gov.uk/assets/downloads/businessandpartners/Trackernet_Data_Services_Guide_Beta_0_2.pdf / 5. Appendix A
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
			// TODO what is the tracker net code?
			Line.TflRail -> "?"
			// TODO what is the tracker net code?
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
			// "Elizabeth line" first to display it (match https://tfl.gov.uk/tube-dlr-overground/status/)
			// "Elizabeth Line" comes from XML feed (http://cloud.tfl.gov.uk/TrackerNet/LineStatus)
			// "Elizabeth" came from XML feed before 2022-11-07 so required to parse historical data.
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
