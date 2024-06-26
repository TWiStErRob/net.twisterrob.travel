package net.twisterrob.travel.statushistory.di

import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.DatastoreOptions
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Prototype
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import net.twisterrob.blt.data.SharedStaticData
import net.twisterrob.blt.data.StaticData
import net.twisterrob.blt.io.feeds.LocalhostUrlBuilder
import net.twisterrob.blt.io.feeds.TFLUrlBuilder
import net.twisterrob.blt.io.feeds.URLBuilder
import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData
import net.twisterrob.blt.model.LineColors
import net.twisterrob.travel.domain.london.status.DomainRefreshUseCase
import net.twisterrob.travel.domain.london.status.DomainStatusHistoryRepository
import net.twisterrob.travel.domain.london.status.api.FeedParser
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase
import net.twisterrob.travel.domain.london.status.api.StatusDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository

@Factory
class Dependencies {

	/**
	 * External dependency from Google.
	 */
	@Singleton
	fun datastore(): Datastore =
		DatastoreOptions.getDefaultInstance().service

	@Bean(preDestroy = "close")
	@Singleton
	fun secretManagerServiceClient(): SecretManagerServiceClient =
		SecretManagerServiceClient.create()

	/**
	 * External dependency from domain layer in common KMP code.
	 */
	@Prototype
	fun historyRepository(
		statusHistoryDataSource: StatusHistoryDataSource,
		statusDataSource: StatusDataSource,
		feedParser: FeedParser,
	): StatusHistoryRepository =
		DomainStatusHistoryRepository(statusHistoryDataSource, statusDataSource, feedParser)

	/**
	 * External dependency from domain layer in common KMP code.
	 */
	@Prototype
	fun refreshUseCase(
		statusHistoryRepository: StatusHistoryRepository,
	): RefreshUseCase =
		DomainRefreshUseCase(statusHistoryRepository)

	@Singleton
	fun staticData(): StaticData =
		SharedStaticData()

	@Singleton
	fun lineColors(staticData: StaticData): LineColors =
		LineColors(staticData.lineColors)

	@Singleton
	fun trackerNetData(): TrackerNetData =
		TrackerNetData()

	@Singleton
	@Requires(notEnv = ["development"])
	fun urlBuilder(trackerNetData: TrackerNetData): URLBuilder =
		TFLUrlBuilder("papp.robert.s@gmail.com", trackerNetData)

	@Singleton
	@Requires(env = ["development"])
	fun urlBuilderDebug(trackerNetData: TrackerNetData): URLBuilder =
		LocalhostUrlBuilder(trackerNetData)
}
