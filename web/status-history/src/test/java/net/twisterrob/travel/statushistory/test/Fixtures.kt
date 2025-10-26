@file:Suppress("TestFunctionName")

package net.twisterrob.travel.statushistory.test

import com.google.cloud.Timestamp
import com.google.cloud.datastore.Entity
import com.google.cloud.datastore.Key
import com.google.cloud.datastore.KeyFactory
import net.twisterrob.travel.domain.london.status.Feed
import net.twisterrob.travel.domain.london.status.Stacktrace
import net.twisterrob.travel.domain.london.status.StatusContent
import net.twisterrob.travel.domain.london.status.StatusItem.FailedStatusItem
import net.twisterrob.travel.domain.london.status.StatusItem.SuccessfulStatusItem
import java.util.UUID
import kotlin.random.Random
import kotlin.time.Instant

internal fun SuccessfulEntity(feed: Feed = randomFeed()): Entity =
	Entity
		.newBuilder(randomKey(feed.name))
		.set("retrievedDate", randomTimestamp())
		.set("content", UUID.randomUUID().toString())
		.build()

internal fun FailedEntity(feed: Feed): Entity =
	Entity
		.newBuilder(randomKey(feed.name))
		.set("retrievedDate", randomTimestamp())
		.set("error", UUID.randomUUID().toString())
		.build()

internal fun SuccessfulStatusItem(): SuccessfulStatusItem =
	SuccessfulStatusItem(
		randomFeed(),
		StatusContent(UUID.randomUUID().toString()),
		randomInstant()
	)

internal fun FailedStatusItem(): FailedStatusItem =
	FailedStatusItem(
		randomFeed(),
		Stacktrace(UUID.randomUUID().toString()),
		randomInstant()
	)

internal fun randomFeed(): Feed =
	Feed.entries[Random.nextInt(Feed.entries.size)]

internal fun randomKey(kind: String): Key {
	val keyFactory = KeyFactory("test-project-id").setKind(kind)
	return keyFactory.newKey(UUID.randomUUID().toString())
}

internal fun randomTimestamp(): Timestamp =
	Timestamp.ofTimeSecondsAndNanos(
		Random.nextLong(Timestamp.MIN_VALUE.seconds, Timestamp.MAX_VALUE.seconds),
		Random.nextInt(Timestamp.MIN_VALUE.nanos, Timestamp.MAX_VALUE.nanos)
	)

internal fun randomInstant(): Instant {
	// Note: Have to use the range from Google's Timestamp, because Instant.DISTANT_PAST/DISTANT_FUTURE
	// are out of range (-100001-12-31T23:59:59.999999999Z - +100000-01-01T00:00:00Z),
	// compared to Timestamp's 0001-01-01T00:00:00Z - 9999-12-31T23:59:59.999999999Z range.
	return Instant.fromEpochSeconds(
		Random.nextLong(Timestamp.MIN_VALUE.seconds, Timestamp.MAX_VALUE.seconds),
		Random.nextInt(Timestamp.MIN_VALUE.nanos, Timestamp.MAX_VALUE.nanos)
	)
}
