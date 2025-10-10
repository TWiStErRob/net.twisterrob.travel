package net.twisterrob.blt.model

import java.util.EnumSet

enum class Line(
	val defaultStopType: StopType,
) {

	unknown(StopType.unknown),
	Bakerloo(StopType.Underground),
	Central(StopType.Underground),
	Circle(StopType.Underground),
	District(StopType.Underground),
	HammersmithAndCity(StopType.Underground),
	Jubilee(StopType.Underground),
	Metropolitan(StopType.Underground),
	Northern(StopType.Underground),
	Piccadilly(StopType.Underground),
	Victoria(StopType.Underground),
	WaterlooAndCity(StopType.Underground),
	DLR(StopType.DLR),

	/**
	 * [Overground] has been split into 6:
	 * * [Liberty]
	 * * [Lioness]
	 * * [Mildmay]
	 * * [Suffragette]
	 * * [Weaver]
	 * * [Windrush]
	 *
	 * See [News](https://tfl.gov.uk/info-for/media/press-releases/2024/february/london-s-overground-lines-to-be-given-new-names-and-colours-in-historic-change-to-capital-s-transport-network)
	 */
	Overground(StopType.Overground),
	Liberty(StopType.Overground),
	Lioness(StopType.Overground),
	Mildmay(StopType.Overground),
	Suffragette(StopType.Overground),
	Weaver(StopType.Overground),
	Windrush(StopType.Overground),
	Tram(StopType.Tram),
	EmiratesAirline(StopType.Air),

	/**
	 * [TflRail] has been rebranded to [ElizabethLine].
	 * See [News](https://tfl.gov.uk/info-for/media/press-releases/2022/may/elizabeth-line-to-open-on-24-may-2022).
	 */
	TflRail(StopType.Rail),
	ElizabethLine(StopType.Rail),
	;

	companion object {

		@JvmField
		val UNDERGROUND: Set<Line> = EnumSet.of(
			Bakerloo,
			Central,
			Circle,
			District,
			HammersmithAndCity,
			Jubilee,
			Metropolitan,
			Northern,
			Piccadilly,
			Victoria,
			WaterlooAndCity
		)

		@JvmStatic
		fun <T, M : MutableMap<Line, in T>> M.fixMap(empty: T): M {
			(Line.entries - this.keys).forEach { this.put(it, empty) }
			return this
		}
	}
}
