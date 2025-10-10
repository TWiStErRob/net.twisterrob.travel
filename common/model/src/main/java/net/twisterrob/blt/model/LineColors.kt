package net.twisterrob.blt.model

@Suppress("DuplicatedCode")
class LineColors(
	private val colors: LineColorScheme,
) {

	@Suppress("detekt.CyclomaticComplexMethod")
	fun getBackground(line: Line): Int =
		when (line) {
			Line.Bakerloo -> colors.bakerlooBackground
			Line.Central -> colors.centralBackground
			Line.Circle -> colors.circleBackground
			Line.District -> colors.districtBackground
			Line.HammersmithAndCity -> colors.hammersmithAndCityBackground
			Line.Jubilee -> colors.jubileeBackground
			Line.Metropolitan -> colors.metropolitanBackground
			Line.Northern -> colors.northernBackground
			Line.Piccadilly -> colors.piccadillyBackground
			Line.Victoria -> colors.victoriaBackground
			Line.WaterlooAndCity -> colors.waterlooAndCityBackground
			Line.DLR -> colors.dlrBackground
			Line.Overground -> @Suppress("DEPRECATION") colors.overgroundBackground
			Line.Liberty -> colors.libertyBackground
			Line.Lioness -> colors.lionessBackground
			Line.Mildmay -> colors.mildmayBackground
			Line.Suffragette -> colors.suffragetteBackground
			Line.Weaver -> colors.weaverBackground
			Line.Windrush -> colors.windrushBackground
			Line.ElizabethLine -> colors.elizabethLineBackground
			Line.EmiratesAirline -> colors.emiratesBackground
			Line.Tram -> colors.tramBackground
			Line.TflRail -> @Suppress("DEPRECATION") colors.tfLRailBackground
			Line.unknown -> colors.unknownBackground
		}

	@Suppress("detekt.CyclomaticComplexMethod")
	fun getForeground(line: Line): Int =
		when (line) {
			Line.Bakerloo -> colors.bakerlooForeground
			Line.Central -> colors.centralForeground
			Line.Circle -> colors.circleForeground
			Line.District -> colors.districtForeground
			Line.HammersmithAndCity -> colors.hammersmithAndCityForeground
			Line.Jubilee -> colors.jubileeForeground
			Line.Metropolitan -> colors.metropolitanForeground
			Line.Northern -> colors.northernForeground
			Line.Piccadilly -> colors.piccadillyForeground
			Line.Victoria -> colors.victoriaForeground
			Line.WaterlooAndCity -> colors.waterlooAndCityForeground
			Line.DLR -> colors.dlrForeground
			Line.Overground -> @Suppress("DEPRECATION") colors.overgroundForeground
			Line.Liberty -> colors.libertyForeground
			Line.Lioness -> colors.lionessForeground
			Line.Mildmay -> colors.mildmayForeground
			Line.Suffragette -> colors.suffragetteForeground
			Line.Weaver -> colors.weaverForeground
			Line.Windrush -> colors.windrushForeground
			Line.ElizabethLine -> colors.elizabethLineForeground
			Line.EmiratesAirline -> colors.emiratesForeground
			Line.Tram -> colors.tramForeground
			Line.TflRail -> @Suppress("DEPRECATION") colors.tfLRailForeground
			Line.unknown -> colors.unknownForeground
		}
}
