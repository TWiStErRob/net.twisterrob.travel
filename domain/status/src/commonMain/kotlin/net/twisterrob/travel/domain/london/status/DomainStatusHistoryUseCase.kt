package net.twisterrob.travel.domain.london.status

import net.twisterrob.travel.domain.london.status.api.ParsedStatusItem
import net.twisterrob.travel.domain.london.status.api.StatusHistoryUseCase
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.changes.Changes
import net.twisterrob.travel.domain.london.status.changes.Result
import net.twisterrob.travel.domain.london.status.changes.ResultChangesCalculator

class DomainStatusHistoryUseCase(
	private val repository: StatusHistoryRepository,
	private val calculator: ResultChangesCalculator,
) : StatusHistoryUseCase {

	override fun history(feed: Feed, max: Int, includeCurrent: Boolean, includeErrors: Boolean): List<Changes> {
		val history = repository.history(feed, max, includeCurrent)
		val results = history
			.filter { includeErrors || it !is ParsedStatusItem.ParseFailed }
			.map(ParsedStatusItem::toResult)
		return calculator.getChanges(results)
	}
}

private fun ParsedStatusItem.toResult(): Result =
	when (this) {
		is ParsedStatusItem.ParsedFeed ->
			Result.ContentResult(this.item.retrievedDate, this.content)

		is ParsedStatusItem.AlreadyFailed ->
			Result.ErrorResult(this.item.retrievedDate, Result.ErrorResult.Error(this.item.error.stacktrace))

		is ParsedStatusItem.ParseFailed ->
			Result.ErrorResult(this.item.retrievedDate, Result.ErrorResult.Error("Error while displaying loaded XML: ${this.error.stacktrace}"))
	}
