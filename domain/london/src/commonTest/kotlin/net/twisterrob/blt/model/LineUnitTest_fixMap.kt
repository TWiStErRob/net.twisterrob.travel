package net.twisterrob.blt.model

import net.twisterrob.blt.model.Line.Companion.fixMap
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.aMapWithSize
import org.hamcrest.Matchers.anEmptyMap
import java.util.LinkedList
import java.util.TreeMap
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class LineUnitTest_fixMap {

	@Test
	fun testFixMapList() {
		val map = TreeMap<Line, List<String>>()
		assertThat(map, anEmptyMap())

		val newMap = map.fixMap(emptyList())

		assertSame(map, newMap)
		assertThat(map, aMapWithSize(Line.entries.size))
		for ((key, value) in map) {
			assertNotNull(key)
			assertNotNull(value)
			assertSame(emptyList(), value)
		}
	}

	@Test
	fun testFixMapListInstance() {
		val map = TreeMap<Line, List<String>>()
		val empty = LinkedList<String>()
		assertThat(map, anEmptyMap())

		val newMap = map.fixMap(empty)

		assertSame(map, newMap)
		assertThat(map, aMapWithSize(Line.entries.size))
		for ((key, value) in map) {
			assertNotNull(key)
			assertNotNull(value)
			assertSame(empty, value)
		}
	}

	@Test
	fun testFixMapLinkedList() {
		val map = HashMap<Line, LinkedList<String>>()
		val empty = LinkedList<String>()
		assertThat(map, anEmptyMap())

		val newMap = map.fixMap(empty)

		assertSame(map, newMap)
		assertThat(map, aMapWithSize(Line.entries.size))
		for ((key, value) in map) {
			assertNotNull(key)
			assertNotNull(value)
			assertSame(empty, value)
		}
	}

	@Test
	fun testFixMapSet() {
		val map = TreeMap<Line, Set<String>>()
		val empty = emptySet<String>()
		assertThat(map, anEmptyMap())

		val newMap = map.fixMap(empty)

		assertSame(map, newMap)
		assertThat(map, aMapWithSize(Line.entries.size))
		for ((key, value) in map) {
			assertNotNull(key)
			assertNotNull(value)
			assertSame(empty, value)
		}
	}

	@Test
	fun testFixMapSetInstance() {
		val map = TreeMap<Line, Set<String>>()
		val empty = emptySet<String>()
		assertThat(map, anEmptyMap())

		val newMap = map.fixMap(empty)

		assertSame(map, newMap)
		assertThat(map, aMapWithSize(Line.entries.size))
		for ((key, value) in map) {
			assertNotNull(key)
			assertNotNull(value)
			assertSame(empty, value)
		}
	}

	@Test
	fun testFixMapHashSet() {
		val map = HashMap<Line, HashSet<String>>()
		val empty = HashSet<String>()
		assertThat(map, anEmptyMap())

		val newMap = map.fixMap(empty)

		assertSame(map, newMap)
		assertThat(map, aMapWithSize(Line.entries.size))
		for ((key, value) in map) {
			assertNotNull(key)
			assertNotNull(value)
			assertSame(empty, value)
		}
	}

	@Test
	fun testFixMapMap() {
		val map = TreeMap<Line, Map<String, Double>>()
		assertThat(map, anEmptyMap())

		val newMap = map.fixMap(emptyMap())

		assertSame(map, newMap)
		assertThat(map, aMapWithSize(Line.entries.size))
		for ((key, value) in map) {
			assertNotNull(key)
			assertNotNull(value)
			assertSame(emptyMap(), value)
		}
	}

	@Test
	fun testFixMapHashMap() {
		val map = HashMap<Line, HashMap<String, Double>>()
		val empty = HashMap<String, Double>()
		assertThat(map, anEmptyMap())

		val newMap = map.fixMap(empty)

		assertSame(map, newMap)
		assertThat(map, aMapWithSize(Line.entries.size))
		for ((key, value) in map) {
			assertNotNull(key)
			assertNotNull(value)
			assertSame(empty, value)
		}
	}
}
