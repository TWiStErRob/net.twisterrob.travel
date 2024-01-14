package net.twisterrob.blt.data.algo.routes;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import net.twisterrob.blt.io.feeds.timetable.StopPoint;

public class Node implements Comparable<Node> {
	StopPoint data;
	final Map<Node, State> in = new TreeMap<>();
	final Map<Node, State> out = new TreeMap<>();
	State state = State.EMPTY;
	public Node(StopPoint data) {
		this.data = data;
	}

	@Override public String toString() {
		return String.format("%s[%s]", data.getName(), state);
	}
	public int compareTo(Node o) {
		return StopPoint.BY_NAME.compare(this.data, o.data);
	}

	enum State {
		EMPTY,
		VISIT,
		CLOSE
	}

	public StopPoint getStop() {
		return this.data;
	}
	public Collection<Node> getOut() {
		return Collections.unmodifiableCollection(out.keySet());
	}
	public Collection<Node> getIn() {
		return Collections.unmodifiableCollection(in.keySet());
	}
}
