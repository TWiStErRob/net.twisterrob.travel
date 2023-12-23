package net.twisterrob.blt.gapp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.Value;

import kotlinx.datetime.Instant;

import net.twisterrob.travel.domain.london.status.Feed;
import net.twisterrob.travel.domain.london.status.Stacktrace;
import net.twisterrob.travel.domain.london.status.StatusContent;
import net.twisterrob.travel.domain.london.status.StatusItem;
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository;

import static net.twisterrob.blt.gapp.FeedConsts.DS_PROP_CONTENT;
import static net.twisterrob.blt.gapp.FeedConsts.DS_PROP_ERROR;
import static net.twisterrob.blt.gapp.FeedConsts.DS_PROP_RETRIEVED_DATE;

public class DatastoreStatusHistoryRepository implements StatusHistoryRepository {

	private final Datastore datastore;
	private final StatusItemToEntityConverter statusItemConverter;
	private final EntityToStatusItemConverter entityConverter;

	public DatastoreStatusHistoryRepository(Datastore datastore) {
		this.datastore = datastore;
		this.statusItemConverter = new StatusItemToEntityConverter(datastore);
		this.entityConverter = new EntityToStatusItemConverter();
	}

	@Override public @Nullable StatusItem getLatest(@Nonnull Feed feed) {
		Query<Entity> q = Query
				.newEntityQueryBuilder()
				.setKind(feed.name())
				.addOrderBy(OrderBy.desc(DS_PROP_RETRIEVED_DATE))
				.build();
		// We're only concerned about the latest one, if any.
		QueryResults<Entity> result = datastore.run(q);
		return result.hasNext()? entityConverter.toItem(result.next()) : null;
	}

	@Override public void add(@Nonnull StatusItem current) {
		datastore.add(statusItemConverter.toEntity(current));
	}

	static @Nonnull Value<?> unindexedString(@Nullable String value) {
		return StatusItemToEntityConverter.unindexedString(value);
	}

	static boolean hasProperty(BaseEntity<?> entry, String propName) {
		return EntityToStatusItemConverter.hasProperty(entry, propName);
	}
}

class StatusItemToEntityConverter {

	private final Datastore datastore;

	StatusItemToEntityConverter(Datastore datastore) {
		this.datastore = datastore;
	}

	public @Nonnull FullEntity<IncompleteKey> toEntity(@Nonnull StatusItem current) {
		KeyFactory keyFactory = datastore.newKeyFactory().setKind(current.getFeed().name());
		FullEntity.Builder<IncompleteKey> newEntry = Entity.newBuilder(keyFactory.newKey());
		newEntry.set(DS_PROP_RETRIEVED_DATE, toTimestamp(current.getRetrievedDate()));
		if (current instanceof StatusItem.SuccessfulStatusItem success) {
			newEntry.set(DS_PROP_CONTENT, unindexedString(success.getContent().getContent()));
		} else if (current instanceof StatusItem.FailedStatusItem error) {
			newEntry.set(DS_PROP_ERROR, unindexedString(error.getError().getStacktrace()));
		}
		return newEntry.build();
	}

	private static @Nonnull Timestamp toTimestamp(@Nonnull Instant instant) {
		return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSeconds(), instant.getNanosecondsOfSecond());
	}

	/**
	 * Strings have a limitation of 1500 bytes when indexed. This removes that limitation.
	 * @see https://cloud.google.com/datastore/docs/concepts/entities#text_string
	 */
	static @Nonnull Value<?> unindexedString(@Nullable String value) {
		return value == null
				? NullValue.of()
				: StringValue.newBuilder(value).setExcludeFromIndexes(true).build();
	}
}

class EntityToStatusItemConverter {

	public @Nonnull StatusItem toItem(@Nonnull Entity entity) {
		if (hasProperty(entity, DS_PROP_CONTENT)) {
			return new StatusItem.SuccessfulStatusItem(
					Feed.valueOf(entity.getKey().getKind()),
					new StatusContent(entity.getString(DS_PROP_CONTENT)),
					toInstant(entity.getTimestamp(DS_PROP_RETRIEVED_DATE))
			);
		} else if (hasProperty(entity, DS_PROP_ERROR)) {
			return new StatusItem.FailedStatusItem(
					Feed.valueOf(entity.getKey().getKind()),
					new Stacktrace(entity.getString(DS_PROP_ERROR)),
					toInstant(entity.getTimestamp(DS_PROP_RETRIEVED_DATE))
			);
		} else {
			throw new IllegalArgumentException("Unknown entity: " + entity);
		}
	}

	private static @Nonnull Instant toInstant(@Nonnull Timestamp timestamp) {
		return Instant.Companion.fromEpochSeconds(timestamp.getSeconds(), timestamp.getNanos());
	}

	static boolean hasProperty(BaseEntity<?> entry, String propName) {
		return entry.getProperties().containsKey(propName);
	}
}
