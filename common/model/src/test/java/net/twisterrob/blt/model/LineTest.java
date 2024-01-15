package net.twisterrob.blt.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class LineTest {

	@Test
	public void testFixMapList() {
		Map<Line, List<String>> map = new TreeMap<>();
		assertThat(map, anEmptyMap());
		Map<Line, List<String>> newMap = Line.fixMap(map, emptyList());
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Map.Entry<Line, List<String>> element : map.entrySet()) {
			assertNotNull(element.getKey());
			List<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(emptyList(), element.getValue());
		}
	}

	@Test
	public void testFixMapListInstance() {
		Map<Line, List<String>> map = new TreeMap<>();
		LinkedList<String> empty = new LinkedList<>();
		assertThat(map, anEmptyMap());
		Map<Line, List<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Map.Entry<Line, List<String>> element : map.entrySet()) {
			assertNotNull(element.getKey());
			List<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, element.getValue());
		}
	}

	@Test
	public void testFixMapLinkedList() {
		HashMap<Line, LinkedList<String>> map = new HashMap<>();
		LinkedList<String> empty = new LinkedList<>();
		assertThat(map, anEmptyMap());
		HashMap<Line, LinkedList<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Map.Entry<Line, LinkedList<String>> element : map.entrySet()) {
			assertNotNull(element.getKey());
			LinkedList<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, value);
		}
	}

	@Test
	public void testFixMapSet() {
		Map<Line, Set<String>> map = new TreeMap<>();
		Set<String> empty = emptySet();
		assertThat(map, anEmptyMap());
		Map<Line, Set<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Map.Entry<Line, Set<String>> element : map.entrySet()) {
			assertNotNull(element.getKey());
			Set<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, element.getValue());
		}
	}

	@Test
	public void testFixMapSetInstance() {
		Map<Line, Set<String>> map = new TreeMap<>();
		Set<String> empty = emptySet();
		assertThat(map, anEmptyMap());
		Map<Line, Set<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Map.Entry<Line, Set<String>> element : map.entrySet()) {
			assertNotNull(element.getKey());
			Set<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, element.getValue());
		}
	}

	@Test
	public void testFixMapHashSet() {
		HashMap<Line, HashSet<String>> map = new HashMap<>();
		HashSet<String> empty = new HashSet<>();
		assertThat(map, anEmptyMap());
		HashMap<Line, HashSet<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Map.Entry<Line, HashSet<String>> element : map.entrySet()) {
			assertNotNull(element.getKey());
			HashSet<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, value);
		}
	}

	@Test
	public void testFixMapMap() {
		Map<Line, Map<String, Double>> map = new TreeMap<>();
		assertThat(map, anEmptyMap());
		Map<Line, Map<String, Double>> newMap = Line.fixMap(map, emptyMap());
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Map.Entry<Line, Map<String, Double>> element : map.entrySet()) {
			assertNotNull(element.getKey());
			Map<String, Double> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(emptyMap(), element.getValue());
		}
	}

	@Test
	public void testFixMapHashMap() {
		HashMap<Line, HashMap<String, Double>> map = new HashMap<>();
		HashMap<String, Double> empty = new HashMap<>();
		assertThat(map, anEmptyMap());
		HashMap<Line, HashMap<String, Double>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Map.Entry<Line, HashMap<String, Double>> element : map.entrySet()) {
			assertNotNull(element.getKey());
			HashMap<String, Double> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, value);
		}
	}
}
