package net.twisterrob.blt.model;

import static org.junit.Assert.*;

import java.util.*;
import java.util.Map.Entry;

import org.junit.Test;

public class LineTest {
	@Test
	public void testFixMapList() {
		Map<Line, List<String>> map = new TreeMap<Line, List<String>>();
		assertEquals(0, map.size());
		Map<Line, List<String>> newMap = Line.fixMap(map, Collections.<String> emptyList());
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Entry<Line, List<String>> element: map.entrySet()) {
			assertNotNull(element.getKey());
			List<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(Collections.emptyList(), element.getValue());
		}
	}
	@Test
	public void testFixMapListInstance() {
		Map<Line, List<String>> map = new TreeMap<Line, List<String>>();
		LinkedList<String> empty = new LinkedList<String>();
		assertEquals(0, map.size());
		Map<Line, List<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Entry<Line, List<String>> element: map.entrySet()) {
			assertNotNull(element.getKey());
			List<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, element.getValue());
		}
	}
	@Test
	public void testFixMapLinkedList() {
		HashMap<Line, LinkedList<String>> map = new HashMap<Line, LinkedList<String>>();
		LinkedList<String> empty = new LinkedList<String>();
		assertEquals(0, map.size());
		HashMap<Line, LinkedList<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Entry<Line, LinkedList<String>> element: map.entrySet()) {
			assertNotNull(element.getKey());
			LinkedList<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, value);
		}
	}
	@Test
	public void testFixMapSet() {
		Map<Line, Set<String>> map = new TreeMap<Line, Set<String>>();
		Set<String> empty = Collections.emptySet();
		assertEquals(0, map.size());
		Map<Line, Set<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Entry<Line, Set<String>> element: map.entrySet()) {
			assertNotNull(element.getKey());
			Set<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, element.getValue());
		}
	}
	@Test
	public void testFixMapSetInstance() {
		Map<Line, Set<String>> map = new TreeMap<Line, Set<String>>();
		Set<String> empty = Collections.emptySet();
		assertEquals(0, map.size());
		Map<Line, Set<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Entry<Line, Set<String>> element: map.entrySet()) {
			assertNotNull(element.getKey());
			Set<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, element.getValue());
		}
	}
	@Test
	public void testFixMapHashSet() {
		HashMap<Line, HashSet<String>> map = new HashMap<Line, HashSet<String>>();
		HashSet<String> empty = new HashSet<String>();
		assertEquals(0, map.size());
		HashMap<Line, HashSet<String>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Entry<Line, HashSet<String>> element: map.entrySet()) {
			assertNotNull(element.getKey());
			HashSet<String> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, value);
		}
	}
	@Test
	public void testFixMapMap() {
		Map<Line, Map<String, Double>> map = new TreeMap<Line, Map<String, Double>>();
		assertEquals(0, map.size());
		Map<Line, Map<String, Double>> newMap = Line.fixMap(map, Collections.<String, Double> emptyMap());
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Entry<Line, Map<String, Double>> element: map.entrySet()) {
			assertNotNull(element.getKey());
			Map<String, Double> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(Collections.emptyMap(), element.getValue());
		}
	}
	@Test
	public void testFixMapHashMap() {
		HashMap<Line, HashMap<String, Double>> map = new HashMap<Line, HashMap<String, Double>>();
		HashMap<String, Double> empty = new HashMap<String, Double>();
		assertEquals(0, map.size());
		HashMap<Line, HashMap<String, Double>> newMap = Line.fixMap(map, empty);
		assertSame(map, newMap);
		assertEquals(Line.values().length, map.size());
		for (Entry<Line, HashMap<String, Double>> element: map.entrySet()) {
			assertNotNull(element.getKey());
			HashMap<String, Double> value = element.getValue(); // force casting
			assertNotNull(value);
			assertSame(empty, value);
		}
	}
}
