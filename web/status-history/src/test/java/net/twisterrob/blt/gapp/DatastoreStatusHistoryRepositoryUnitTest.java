package net.twisterrob.blt.gapp;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.BaseKey;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;

import kotlin.random.Random;
import kotlinx.datetime.Instant;

import net.twisterrob.blt.gapp.infrastructure.DatastoreStatusHistoryRepository;
import net.twisterrob.travel.domain.london.status.Feed;
import net.twisterrob.travel.domain.london.status.Stacktrace;
import net.twisterrob.travel.domain.london.status.StatusContent;
import net.twisterrob.travel.domain.london.status.StatusItem;
import net.twisterrob.travel.domain.london.status.StatusItem.FailedStatusItem;
import net.twisterrob.travel.domain.london.status.StatusItem.SuccessfulStatusItem;
import net.twisterrob.travel.domain.london.status.api.StatusHistoryRepository;

public class DatastoreStatusHistoryRepositoryUnitTest {

	private final Datastore mockDatastore = mock(Datastore.class);
	private final StatusHistoryRepository subject = new DatastoreStatusHistoryRepository(mockDatastore);

	@Test public void testSavesSuccessfulItem() {
		when(mockDatastore.newKeyFactory()).thenReturn(new KeyFactory("test-project-id"));

		SuccessfulStatusItem item = SuccessfulStatusItem();

		subject.add(item);

		FullEntity<? extends BaseKey> entity = captureAddedEntity(mockDatastore);
		assertEquals(item.getFeed().name(), entity.getKey().getKind());
		Map<String, Value<?>> props = entity.getProperties();
		assertThat(props.entrySet(), hasSize(2));
		Instant now = item.getRetrievedDate();
		Timestamp ts = Timestamp.ofTimeSecondsAndNanos(now.getEpochSeconds(), now.getNanosecondsOfSecond());
		assertThat(props, hasEntry("retrievedDate", TimestampValue.of(ts)));
		String content = item.getContent().getContent();
		assertThat(props, hasEntry("content", StringValue.newBuilder(content).setExcludeFromIndexes(true).build()));
	}

	@Test public void testSavesFailedItem() {
		when(mockDatastore.newKeyFactory()).thenReturn(new KeyFactory("test-project-id"));
		FailedStatusItem item = FailedStatusItem();

		subject.add(item);

		FullEntity<? extends BaseKey> entity = captureAddedEntity(mockDatastore);
		assertEquals(item.getFeed().name(), entity.getKey().getKind());
		Map<String, Value<?>> props = entity.getProperties();
		assertThat(props.entrySet(), hasSize(2));
		Instant now = item.getRetrievedDate();
		Timestamp ts = Timestamp.ofTimeSecondsAndNanos(now.getEpochSeconds(), now.getNanosecondsOfSecond());
		assertThat(props, hasEntry("retrievedDate", TimestampValue.of(ts)));
		String error = item.getError().getStacktrace();
		assertThat(props, hasEntry("error", StringValue.newBuilder(error).setExcludeFromIndexes(true).build()));
	}

	@Test public void testQuery() {
		@SuppressWarnings("unchecked")
		QueryResults<Entity> mockResults = mock(QueryResults.class);
		doAnswerIterator(mockResults, Collections.emptyIterator());
		when(mockDatastore.run(ArgumentMatchers.<EntityQuery>any())).thenReturn(mockResults);

		Feed feed = randomFeed();
		subject.getAll(feed, 2);

		EntityQuery query = captureRanQuery(mockDatastore);
		assertEquals(feed.name(), query.getKind());
		assertEquals(List.of(OrderBy.desc("retrievedDate")), query.getOrderBy());
		assertEquals(Integer.valueOf(2), query.getLimit());
	}

	@Test public void testLimitShortensLongerList() {
		List<Entity> list = List.of(SuccessfulEntity(), SuccessfulEntity(), SuccessfulEntity());
		@SuppressWarnings("unchecked")
		QueryResults<Entity> mockResults = mock(QueryResults.class);
		doAnswerIterator(mockResults, list.iterator());
		when(mockDatastore.run(ArgumentMatchers.<EntityQuery>any())).thenReturn(mockResults);

		List<StatusItem> result = subject.getAll(randomFeed(), 2);

		assertThat(result, hasSize(2));
	}

	@Test public void testReturnsSuccessfulEntityData() {
		Feed feed = randomFeed();
		Entity entity = SuccessfulEntity(feed);
		@SuppressWarnings("unchecked")
		QueryResults<Entity> mockResults = mock(QueryResults.class);
		List<Entity> list = List.of(entity);
		doAnswerIterator(mockResults, list.iterator());
		when(mockDatastore.run(ArgumentMatchers.<EntityQuery>any())).thenReturn(mockResults);

		List<StatusItem> result = subject.getAll(randomFeed(), 2);

		StatusItem item = result.get(0);
		assertEquals(feed, item.getFeed());
		Timestamp ts = entity.getTimestamp("retrievedDate");
		Instant instant = Instant.Companion.fromEpochSeconds(ts.getSeconds(), ts.getNanos());
		assertEquals(instant, item.getRetrievedDate());
		assertThat(item, instanceOf(SuccessfulStatusItem.class));
		assertEquals(entity.getString("content"), ((SuccessfulStatusItem)item).getContent().getContent());
	}

	@Test public void testReturnsFailedEntityData() {
		Feed feed = randomFeed();
		Entity entity = FailedEntity(feed);
		List<Entity> list = List.of(entity);
		@SuppressWarnings("unchecked")
		QueryResults<Entity> mockResults = mock(QueryResults.class);
		doAnswerIterator(mockResults, list.iterator());
		when(mockDatastore.run(ArgumentMatchers.<EntityQuery>any())).thenReturn(mockResults);

		List<StatusItem> result = subject.getAll(randomFeed(), 2);

		StatusItem item = result.get(0);
		assertEquals(feed, item.getFeed());
		Timestamp ts = entity.getTimestamp("retrievedDate");
		Instant instant = Instant.Companion.fromEpochSeconds(ts.getSeconds(), ts.getNanos());
		assertEquals(instant, item.getRetrievedDate());
		assertThat(item, instanceOf(FailedStatusItem.class));
		assertEquals(entity.getString("error"), ((FailedStatusItem)item).getError().getStacktrace());
	}

	@Test public void testSymmetricSaveLoadSuccessfulItem() {
		SuccessfulStatusItem item = SuccessfulStatusItem();
		when(mockDatastore.newKeyFactory()).thenReturn(new KeyFactory("test-project-id"));
		subject.add(item);
		FullEntity<? extends BaseKey> savedEntity = captureAddedEntity(mockDatastore);
		Entity entity = Entity.newBuilder(randomKey(item.getFeed().name()), savedEntity).build();

		@SuppressWarnings("unchecked")
		QueryResults<Entity> mockResults = mock(QueryResults.class);
		doAnswerIterator(mockResults, List.of(entity).iterator());
		when(mockDatastore.run(ArgumentMatchers.<EntityQuery>any())).thenReturn(mockResults);

		List<StatusItem> result = subject.getAll(randomFeed(), 1);
		assertEquals(item, result.get(0));
	}

	private static <T> void doAnswerIterator(Iterator<? super T> mock, Iterator<T> value) {
		doAnswer(invocation -> value.next()).when(mock).next();
		doAnswer(invocation -> value.hasNext()).when(mock).hasNext();
	}

	private static EntityQuery captureRanQuery(Datastore mockDatastore) {
		ArgumentCaptor<EntityQuery> captor = ArgumentCaptor.forClass(EntityQuery.class);
		verify(mockDatastore).run(captor.capture());
		assertThat(captor.getAllValues(), hasSize(1));
		return captor.getAllValues().get(0);
	}

	private static FullEntity<? extends BaseKey> captureAddedEntity(Datastore mockDatastore) {
		@SuppressWarnings("unchecked")
		ArgumentCaptor<FullEntity<? extends BaseKey>> captor = ArgumentCaptor.forClass(FullEntity.class);
		verify(mockDatastore).add(captor.capture());
		assertThat(captor.getAllValues(), hasSize(1));
		return captor.getAllValues().get(0);
	}

	private static Entity SuccessfulEntity() {
		return SuccessfulEntity(randomFeed());
	}

	private static Entity SuccessfulEntity(Feed feed) {
		return Entity
				.newBuilder(randomKey(feed.name()))
				.set("retrievedDate", randomTimestamp())
				.set("content", UUID.randomUUID().toString())
				.build();
	}

	private static Entity FailedEntity(Feed feed) {
		return Entity
				.newBuilder(randomKey(feed.name()))
				.set("retrievedDate", randomTimestamp())
				.set("error", UUID.randomUUID().toString())
				.build();
	}

	private static StatusItem.SuccessfulStatusItem SuccessfulStatusItem() {
		return new StatusItem.SuccessfulStatusItem(
				randomFeed(),
				new StatusContent(UUID.randomUUID().toString()),
				randomInstant()
		);
	}

	private static StatusItem.FailedStatusItem FailedStatusItem() {
		return new StatusItem.FailedStatusItem(
				randomFeed(),
				new Stacktrace(UUID.randomUUID().toString()),
				randomInstant()
		);
	}

	private static Feed randomFeed() {
		return Feed.getEntries().get(Random.Default.nextInt(Feed.getEntries().size()));
	}

	private static Key randomKey(String kind) {
		KeyFactory keyFactory = new KeyFactory("test-project-id").setKind(kind);
		return keyFactory.newKey(UUID.randomUUID().toString());
	}

	private static Timestamp randomTimestamp() {
		return Timestamp.ofTimeSecondsAndNanos(
				Random.Default.nextLong(
						Timestamp.MIN_VALUE.getSeconds(),
						Timestamp.MAX_VALUE.getSeconds()
				),
				Random.Default.nextInt(
						Timestamp.MIN_VALUE.getNanos(),
						Timestamp.MAX_VALUE.getNanos()
				)
		);
	}

	private static Instant randomInstant() {
		System.out.println(Instant.Companion.getDISTANT_PAST());
		System.out.println(Instant.Companion.getDISTANT_FUTURE());
		System.out.println(Timestamp.MIN_VALUE);
		System.out.println(Timestamp.MAX_VALUE);
		// Note: Have to use the range from Google's Timestamp, because Instant.DISTANT_PAST/DISTANT_FUTURE
		// are out of range (-100001-12-31T23:59:59.999999999Z - +100000-01-01T00:00:00Z),
		// compared to Timestamp's 0001-01-01T00:00:00Z - 9999-12-31T23:59:59.999999999Z range.
		return Instant.Companion.fromEpochSeconds(
				Random.Default.nextLong(
						Timestamp.MIN_VALUE.getSeconds(),
						Timestamp.MAX_VALUE.getSeconds()
				),
				Random.Default.nextInt(
						Timestamp.MIN_VALUE.getNanos(),
						Timestamp.MAX_VALUE.getNanos()
				)
		);
	}
}
