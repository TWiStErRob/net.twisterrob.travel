package net.twisterrob.travel.statushistory.infrastructure

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.EntityQuery
import com.google.cloud.datastore.FullEntity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.KeyFactory
import com.google.cloud.datastore.QueryResults
import com.google.cloud.datastore.StringValue
import com.google.cloud.datastore.StructuredQuery
import com.google.cloud.datastore.TimestampValue
import kotlinx.datetime.Instant
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.Stacktrace
import net.twisterrob.travel.domain.london.status.StatusContent
import net.twisterrob.travel.domain.london.status.StatusItem.FailedStatusItem
import net.twisterrob.travel.domain.london.status.StatusItem.SuccessfulStatusItem
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasEntry
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Collections.emptyIterator
import java.util.UUID
import kotlin.random.Random

class DatastoreStatusHistoryRepositoryUnitTest {

	private val mockDatastore: Datastore = mock()
	private val subject: StatusHistoryRepository = DatastoreStatusHistoryRepository(mockDatastore)

	@Test fun testSavesSuccessfulItem() {
		`when`(mockDatastore.newKeyFactory()).thenReturn(KeyFactory("test-project-id"))

		val item = SuccessfulStatusItem()

		subject.add(item)

		val entity: FullEntity<*> = captureSingle { verify(mockDatastore).add(capture()) }
		assertEquals(item.feed.name, entity.key!!.kind)
		val props = entity.properties
		assertThat(props.entries, hasSize(2))
		val now = item.retrievedDate
		val ts = Timestamp.ofTimeSecondsAndNanos(now.epochSeconds, now.nanosecondsOfSecond)
		assertThat(props, hasEntry("retrievedDate", TimestampValue.of(ts)))
		val content = item.content.content
		assertThat(props, hasEntry("content", StringValue.newBuilder(content).setExcludeFromIndexes(true).build()))
	}

	@Test fun testSavesFailedItem() {
		`when`(mockDatastore.newKeyFactory()).thenReturn(KeyFactory("test-project-id"))
		val item = FailedStatusItem()

		subject.add(item)

		val entity: FullEntity<*> = captureSingle { verify(mockDatastore).add(capture()) }
		assertEquals(item.feed.name, entity.key!!.kind)
		val props = entity.properties
		assertThat(props.entries, hasSize(2))
		val now = item.retrievedDate
		val ts = Timestamp.ofTimeSecondsAndNanos(now.epochSeconds, now.nanosecondsOfSecond)
		assertThat(props, hasEntry("retrievedDate", TimestampValue.of(ts)))
		val error = item.error.stacktrace
		assertThat(props, hasEntry("error", StringValue.newBuilder(error).setExcludeFromIndexes(true).build()))
	}

	@Test fun testQuery() {
		val mockResults: QueryResults<Entity> = mock()
		doAnswerIterator(mockResults, emptyIterator())
		`when`(mockDatastore.run(any<EntityQuery>())).thenReturn(mockResults)

		val feed = randomFeed()
		subject.getAll(feed, 2)

		val query: EntityQuery = captureSingle { verify(mockDatastore).run(capture()) }
		assertEquals(feed.name, query.kind)
		assertEquals(listOf(StructuredQuery.OrderBy.desc("retrievedDate")), query.orderBy)
		assertEquals(2, query.limit)
	}

	@Test fun testLimitShortensLongerList() {
		val list = listOf(SuccessfulEntity(), SuccessfulEntity(), SuccessfulEntity())
		val mockResults: QueryResults<Entity> = mock()
		val iterator = spy(list.iterator())
		doAnswerIterator(mockResults, iterator)
		`when`(mockDatastore.run(any<EntityQuery>())).thenReturn(mockResults)

		val result = subject.getAll(randomFeed(), 2)

		assertThat(result, hasSize(2))
		verify(iterator, times(2)).next()
	}

	@Test fun testReturnsSuccessfulEntityData() {
		val feed = randomFeed()
		val entity = SuccessfulEntity(feed)
		val mockResults: QueryResults<Entity> = mock()
		val list = listOf(entity)
		doAnswerIterator(mockResults, list.iterator())
		`when`(mockDatastore.run(any<EntityQuery>())).thenReturn(mockResults)

		val result = subject.getAll(randomFeed(), 2)

		val item = result[0]
		assertEquals(feed, item.feed)
		val ts = entity.getTimestamp("retrievedDate")
		val instant = Instant.fromEpochSeconds(ts.seconds, ts.nanos)
		assertEquals(instant, item.retrievedDate)
		assertThat(item, instanceOf(SuccessfulStatusItem::class.java))
		assertEquals(entity.getString("content"), (item as SuccessfulStatusItem).content.content)
	}

	@Test fun testReturnsFailedEntityData() {
		val feed = randomFeed()
		val entity = FailedEntity(feed)
		val list = listOf(entity)
		val mockResults: QueryResults<Entity> = mock()
		doAnswerIterator(mockResults, list.iterator())
		`when`(mockDatastore.run(any<EntityQuery>())).thenReturn(mockResults)

		val result = subject.getAll(randomFeed(), 2)

		val item = result[0]
		assertEquals(feed, item.feed)
		val ts = entity.getTimestamp("retrievedDate")
		val instant = Instant.fromEpochSeconds(ts.seconds, ts.nanos)
		assertEquals(instant, item.retrievedDate)
		assertThat(item, instanceOf(FailedStatusItem::class.java))
		assertEquals(entity.getString("error"), (item as FailedStatusItem).error.stacktrace)
	}

	@Test fun testSymmetricSaveLoadSuccessfulItem() {
		val item = SuccessfulStatusItem()
		`when`(mockDatastore.newKeyFactory()).thenReturn(KeyFactory("test-project-id"))
		subject.add(item)
		val savedEntity: FullEntity<*> = captureSingle { verify(mockDatastore).add(capture()) }
		val entity = Entity.newBuilder(randomKey(item.feed.name), savedEntity).build()

		val mockResults: QueryResults<Entity> = mock()
		doAnswerIterator(mockResults, listOf(entity).iterator())
		`when`(mockDatastore.run(any<EntityQuery>())).thenReturn(mockResults)

		val result = subject.getAll(randomFeed(), 1)
		assertEquals(item, result[0])
	}

	companion object {

		private fun <T : Any> doAnswerIterator(mock: Iterator<T>, value: Iterator<T>) {
			doAnswer { value.next() }.`when`(mock).next()
			doAnswer { value.hasNext() }.`when`(mock).hasNext()
		}

		private fun <T> captureSingle(block: ArgumentCaptor<T>.() -> Unit): T {
			val captor: ArgumentCaptor<T> = ArgumentCaptor.captor()
			block(captor)
			assertThat(captor.allValues, hasSize(1))
			return captor.allValues.single()
		}

		@Suppress("TestFunctionName")
		private fun SuccessfulEntity(feed: Feed = randomFeed()): Entity =
			Entity
				.newBuilder(randomKey(feed.name))
				.set("retrievedDate", randomTimestamp())
				.set("content", UUID.randomUUID().toString())
				.build()

		@Suppress("TestFunctionName")
		private fun FailedEntity(feed: Feed): Entity =
			Entity
				.newBuilder(randomKey(feed.name))
				.set("retrievedDate", randomTimestamp())
				.set("error", UUID.randomUUID().toString())
				.build()

		@Suppress("TestFunctionName")
		private fun SuccessfulStatusItem(): SuccessfulStatusItem =
			SuccessfulStatusItem(
				randomFeed(),
				StatusContent(UUID.randomUUID().toString()),
				randomInstant()
			)

		@Suppress("TestFunctionName")
		private fun FailedStatusItem(): FailedStatusItem =
			FailedStatusItem(
				randomFeed(),
				Stacktrace(UUID.randomUUID().toString()),
				randomInstant()
			)

		private fun randomFeed(): Feed =
			Feed.entries[Random.nextInt(Feed.entries.size)]

		private fun randomKey(kind: String): Key {
			val keyFactory = KeyFactory("test-project-id").setKind(kind)
			return keyFactory.newKey(UUID.randomUUID().toString())
		}

		private fun randomTimestamp(): Timestamp =
			Timestamp.ofTimeSecondsAndNanos(
				Random.nextLong(Timestamp.MIN_VALUE.seconds, Timestamp.MAX_VALUE.seconds),
				Random.nextInt(Timestamp.MIN_VALUE.nanos, Timestamp.MAX_VALUE.nanos)
			)

		private fun randomInstant(): Instant {
			println(Instant.DISTANT_PAST)
			println(Instant.DISTANT_FUTURE)
			println(Timestamp.MIN_VALUE)
			println(Timestamp.MAX_VALUE)
			// Note: Have to use the range from Google's Timestamp, because Instant.DISTANT_PAST/DISTANT_FUTURE
			// are out of range (-100001-12-31T23:59:59.999999999Z - +100000-01-01T00:00:00Z),
			// compared to Timestamp's 0001-01-01T00:00:00Z - 9999-12-31T23:59:59.999999999Z range.
			return Instant.fromEpochSeconds(
				Random.nextLong(Timestamp.MIN_VALUE.seconds, Timestamp.MAX_VALUE.seconds),
				Random.nextInt(Timestamp.MIN_VALUE.nanos, Timestamp.MAX_VALUE.nanos)
			)
		}
	}
}
