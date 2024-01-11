@file:Suppress("PackageDirectoryMismatch") // Keep it close to Mocks.kt

package io.mockative

import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusDataSource

inline fun <reified T : Any> mock(): T =
	when (T::class) {
		StatusHistoryRepository::class -> mock(StatusHistoryRepository::class) as T
		StatusHistoryDataSource::class -> mock(StatusHistoryDataSource::class) as T
		StatusDataSource::class -> mock(StatusDataSource::class) as T
		FeedParser::class -> mock(FeedParser::class) as T
		else -> throw IllegalArgumentException("No mock for ${T::class}")
	}
