package net.twisterrob.blt.gapp;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.annotation.Requires;
import io.micronaut.runtime.http.scope.RequestScope;
import jakarta.inject.Singleton;

import net.twisterrob.blt.data.SharedStaticData;
import net.twisterrob.blt.data.StaticData;
import net.twisterrob.blt.io.feeds.LocalhostUrlBuilder;
import net.twisterrob.blt.io.feeds.TFLUrlBuilder;
import net.twisterrob.blt.io.feeds.URLBuilder;
import net.twisterrob.travel.domain.london.status.DomainHistoryUseCase;
import net.twisterrob.travel.domain.london.status.DomainRefreshUseCase;
import net.twisterrob.travel.domain.london.status.api.FeedParser;
import net.twisterrob.travel.domain.london.status.api.HistoryUseCase;
import net.twisterrob.travel.domain.london.status.api.RefreshUseCase;
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository;
import net.twisterrob.travel.domain.london.status.api.StatusInteractor;

@Factory
public class Dependencies {

	/**
	 * External dependency from Google.
	 */
	@Singleton
	public Datastore datastore() {
		return DatastoreOptions.getDefaultInstance().getService();
	}

	/**
	 * External dependency from domain layer in common KMP code.
	 */
	@Prototype
	public HistoryUseCase historyUseCase(
			StatusHistoryRepository statusHistoryRepository,
			StatusInteractor statusInteractor,
			FeedParser feedParser
	) {
		return new DomainHistoryUseCase(statusHistoryRepository, statusInteractor, feedParser);
	}

	/**
	 * External dependency from domain layer in common KMP code.
	 */
	@Prototype
	public RefreshUseCase refreshUseCase(
			StatusHistoryRepository statusHistoryRepository,
			StatusInteractor statusInteractor
	) {
		return new DomainRefreshUseCase(statusHistoryRepository, statusInteractor);
	}

	@Singleton
	public StaticData staticData() {
		return new SharedStaticData();
	}

	@Singleton
	@Requires(notEnv = "development")
	public URLBuilder urlBuilder() {
		return new TFLUrlBuilder("papp.robert.s@gmail.com");
	}

	@Singleton
	@Requires(env = "development")
	public URLBuilder urlBuilderDebug() {
		return new LocalhostUrlBuilder();
	}
}
