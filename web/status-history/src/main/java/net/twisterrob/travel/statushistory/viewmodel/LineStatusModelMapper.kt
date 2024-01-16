package net.twisterrob.travel.statushistory.viewmodel

import jakarta.inject.Inject
import net.twisterrob.travel.domain.london.status.changes.Changes

class LineStatusModelMapper @Inject constructor(
	private val resultChangeModelMapper: ResultChangeModelMapper,
	private val lineColorsModelMapper: LineColorsModelMapper,
) {

	fun map(changes: List<Changes>): LineStatusModel =
		LineStatusModel(
			changes.map(resultChangeModelMapper::map),
			lineColorsModelMapper.map()
		)
}
