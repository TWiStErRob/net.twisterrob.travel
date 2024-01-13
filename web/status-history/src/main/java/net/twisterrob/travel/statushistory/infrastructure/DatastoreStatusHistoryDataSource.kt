package net.twisterrob.travel.statushistory.infrastructure

import com.google.cloud.Timestamp
import com.google.cloud.datastore.BaseEntity
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.IncompleteKey
import com.google.cloud.datastore.NullValue
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StringValue
import com.google.cloud.datastore.StructuredQuery
import com.google.cloud.datastore.Value
import io.micronaut.context.annotation.Bean
import kotlinx.datetime.Instant
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.Stacktrace
import net.twisterrob.travel.domain.london.status.StatusContent
import net.twisterrob.travel.domain.london.status.StatusItem
import net.twisterrob.travel.domain.london.status.api.StatusHistoryDataSource
import net.twisterrob.travel.statushistory.infrastructure.DatastoreStatusHistoryDataSource.Companion.DS_PROP_CONTENT
import net.twisterrob.travel.statushistory.infrastructure.DatastoreStatusHistoryDataSource.Companion.DS_PROP_ERROR
import net.twisterrob.travel.statushistory.infrastructure.DatastoreStatusHistoryDataSource.Companion.DS_PROP_RETRIEVED_DATE

@Bean(typed = [StatusHistoryDataSource::class])
class DatastoreStatusHistoryDataSource(
	private val datastore: Datastore,
) : StatusHistoryDataSource {

	private val statusItemConverter = StatusItemToEntityConverter(datastore)
	private val entityConverter = EntityToStatusItemConverter()

	override fun add(current: StatusItem) {
		datastore.add(statusItemConverter.toEntity(current))
	}

	override fun getAll(feed: Feed, max: Int): List<StatusItem> =
		datastore.run(queryAllMostRecentFirst(feed, max))
			.asSequence()
			.take(max)
			.map { entityConverter.toItem(it) }
			.toList()

	companion object {

		const val DS_PROP_RETRIEVED_DATE: String = "retrievedDate"
		const val DS_PROP_CONTENT: String = "content"
		const val DS_PROP_ERROR: String = "error"

		private fun queryAllMostRecentFirst(feed: Feed, limit: Int): Query<Entity> =
			Query
				.newEntityQueryBuilder()
				.setKind(feed.name)
				.setLimit(limit)
				.addOrderBy(StructuredQuery.OrderBy.desc(DS_PROP_RETRIEVED_DATE))
				.build()
	}
}

private class StatusItemToEntityConverter(
	private val datastore: Datastore,
) {

	fun toEntity(current: StatusItem): FullEntity<IncompleteKey> {
		val keyFactory = datastore.newKeyFactory().setKind(current.feed.name)
		val newEntry = Entity.newBuilder(keyFactory.newKey())
		newEntry[DS_PROP_RETRIEVED_DATE] = current.retrievedDate.toTimestamp()
		when (current) {
			is StatusItem.SuccessfulStatusItem -> {
				newEntry[DS_PROP_CONTENT] = current.content.content.asUnindexedString()
			}

			is StatusItem.FailedStatusItem -> {
				newEntry[DS_PROP_ERROR] = current.error.stacktrace.asUnindexedString()
			}
		}
		return newEntry.build()
	}

	companion object {

		private fun Instant.toTimestamp(): Timestamp =
			Timestamp.ofTimeSecondsAndNanos(epochSeconds, nanosecondsOfSecond)

		/**
		 * Strings have a limitation of 1500 bytes when indexed. This removes that limitation.
		 * See [Entities](https://cloud.google.com/datastore/docs/concepts/entities#text_string).
		 */
		private fun String?.asUnindexedString(): Value<*> =
			when (this) {
				null -> NullValue.of()
				else -> StringValue.newBuilder(this).setExcludeFromIndexes(true).build()
			}
	}
}

private class EntityToStatusItemConverter {

	fun toItem(entity: Entity): StatusItem =
		when {
			entity.hasProperty(DS_PROP_CONTENT) -> {
				StatusItem.SuccessfulStatusItem(
					Feed.valueOf(entity.key.kind),
					StatusContent(entity.getString(DS_PROP_CONTENT)),
					entity.getTimestamp(DS_PROP_RETRIEVED_DATE).toInstant()
				)
			}

			entity.hasProperty(DS_PROP_ERROR) -> {
				StatusItem.FailedStatusItem(
					Feed.valueOf(entity.key.kind),
					Stacktrace(entity.getString(DS_PROP_ERROR)),
					entity.getTimestamp(DS_PROP_RETRIEVED_DATE).toInstant()
				)
			}

			else -> {
				throw IllegalArgumentException("Unknown entity: ${entity}")
			}
		}

	companion object {

		private fun Timestamp.toInstant(): Instant =
			Instant.fromEpochSeconds(this.seconds, this.nanos)

		private fun BaseEntity<*>.hasProperty(propName: String?): Boolean =
			this.properties.containsKey(propName)
	}
}
