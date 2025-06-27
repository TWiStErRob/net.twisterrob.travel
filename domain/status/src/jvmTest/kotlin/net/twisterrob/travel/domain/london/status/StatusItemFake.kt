package net.twisterrob.travel.domain.london.status

import java.util.UUID
import kotlin.random.Random
import kotlin.time.Instant

fun SuccessfulStatusItem(): StatusItem.SuccessfulStatusItem =
	StatusItem.SuccessfulStatusItem(
		Feed.entries[Random.nextInt(Feed.entries.size)],
		StatusContent(UUID.randomUUID().toString()),
		Instant.fromEpochMilliseconds(Random.nextLong()),
	)

fun FailedStatusItem(): StatusItem.FailedStatusItem =
	StatusItem.FailedStatusItem(
		Feed.entries[Random.nextInt(Feed.entries.size)],
		Stacktrace(UUID.randomUUID().toString()),
		Instant.fromEpochMilliseconds(Random.nextLong()),
	)
