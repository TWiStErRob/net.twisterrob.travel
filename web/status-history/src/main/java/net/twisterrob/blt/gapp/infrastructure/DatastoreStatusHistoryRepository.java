package net.twisterrob.blt.gapp.infrastructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import io.micronaut.context.annotation.Bean;
import kotlinx.datetime.Instant;

import net.twisterrob.travel.domain.london.status.Feed;
import net.twisterrob.travel.domain.london.status.Stacktrace;
import net.twisterrob.travel.domain.london.status.StatusContent;
import net.twisterrob.travel.domain.london.status.StatusItem;
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository;

import static net.twisterrob.blt.gapp.infrastructure.DatastoreStatusHistoryRepository.DS_PROP_CONTENT;
import static net.twisterrob.blt.gapp.infrastructure.DatastoreStatusHistoryRepository.DS_PROP_ERROR;
import static net.twisterrob.blt.gapp.infrastructure.DatastoreStatusHistoryRepository.DS_PROP_RETRIEVED_DATE;

@Bean(typed = StatusHistoryRepository.class)
public class DatastoreStatusHistoryRepository implements StatusHistoryRepository {

	static final String DS_PROP_RETRIEVED_DATE = "retrievedDate";
	static final String DS_PROP_CONTENT = "content";
	static final String DS_PROP_ERROR = "error";

	private final Datastore datastore;
	private final StatusItemToEntityConverter statusItemConverter;
	private final EntityToStatusItemConverter entityConverter;

	public DatastoreStatusHistoryRepository(Datastore datastore) {
		this.datastore = datastore;
		this.statusItemConverter = new StatusItemToEntityConverter(datastore);
		this.entityConverter = new EntityToStatusItemConverter();
	}

	@Override public void add(@Nonnull StatusItem current) {
		datastore.add(statusItemConverter.toEntity(current));
	}

	@Override public @Nonnull List<StatusItem> getAll(@Nonnull Feed feed, int max) {
		Iterator<Entity> entries = queryAllMostRecentFirst(feed, max);
		List<StatusItem> results = new ArrayList<>();
		while (entries.hasNext()) {
			if (--max < 0) {
				break; // We've had enough, don't read more.
			}
			Entity entry = entries.next();
			StatusItem result = entityConverter.toItem(entry);
			results.add(result);
		}
		return results;
	}

	private @Nonnull QueryResults<Entity> queryAllMostRecentFirst(@Nonnull Feed feed, int limit) {
		Query<Entity> query = Query
				.newEntityQueryBuilder()
				.setKind(feed.name())
				.setLimit(limit)
				.addOrderBy(OrderBy.desc(DS_PROP_RETRIEVED_DATE))
				.build()
				;
		return datastore.run(query);
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
		} else {
			throw new IllegalArgumentException("Unknown item: " + current);
		}
		return newEntry.build();
	}

	private static @Nonnull Timestamp toTimestamp(@Nonnull Instant instant) {
		return Timestamp.ofTimeSecondsAndNanos(instant.getEpochSeconds(), instant.getNanosecondsOfSecond());
	}

	/**
	 * Strings have a limitation of 1500 bytes when indexed. This removes that limitation.
	 * @see <a href="https://cloud.google.com/datastore/docs/concepts/entities#text_string">Entities</a>
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
