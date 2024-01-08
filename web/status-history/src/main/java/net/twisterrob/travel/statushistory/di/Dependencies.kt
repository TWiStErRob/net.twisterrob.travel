package net.twisterrob.travel.statushistory.di

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreOptions
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import net.twisterrob.blt.data.SharedStaticData
import net.twisterrob.blt.data.StaticData
import net.twisterrob.blt.io.feeds.LocalhostUrlBuilder
import net.twisterrob.blt.io.feeds.TFLUrlBuilder
import net.twisterrob.blt.io.feeds.URLBuilder
import net.twisterrob.travel.domain.london.status.DomainHistoryRepository
import net.twisterrob.travel.domain.london.status.DomainHistoryUseCase
import net.twisterrob.travel.domain.london.status.DomainRefreshUseCase
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.HistoryUseCase
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource

@Factory
class Dependencies {

	/**
	 * External dependency from Google.
	 */
	@Singleton
	fun datastore(): Datastore =
		DatastoreOptions.getDefaultInstance().service

	/**
	 * External dependency from domain layer in common KMP code.
	 */
	@Prototype
	fun historyUseCase(
		statusHistoryDataSource: StatusHistoryDataSource,
		statusDataSource: StatusDataSource,
		feedParser: FeedParser,
	): HistoryUseCase =
		DomainHistoryUseCase(DomainHistoryRepository(statusHistoryDataSource, statusDataSource, feedParser))

	/**
	 * External dependency from domain layer in common KMP code.
	 */
	@Prototype
	fun refreshUseCase(
		statusHistoryDataSource: StatusHistoryDataSource,
		statusDataSource: StatusDataSource,
	): RefreshUseCase =
		DomainRefreshUseCase(statusHistoryDataSource, statusDataSource)

	@Singleton
	fun staticData(): StaticData =
		SharedStaticData()

	@Singleton
	@Requires(notEnv = ["development"])
	fun urlBuilder(): URLBuilder =
		TFLUrlBuilder("papp.robert.s@gmail.com")

	@Singleton
	@Requires(env = ["development"])
	fun urlBuilderDebug(): URLBuilder =
		LocalhostUrlBuilder()
}
