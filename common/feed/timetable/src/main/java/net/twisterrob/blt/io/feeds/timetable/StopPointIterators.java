package net.twisterrob.blt.io.feeds.timetable;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class StopPointIterators implements Iterator<StopPoint> {
	private Iterator<? extends Iterable<StopPoint>> it;
	private Iterator<StopPoint> current = null;
	public StopPointIterators(Iterator<? extends Iterable<StopPoint>> iterator) {
		this.it = iterator;
	}
	public boolean hasNext() {
		if (current == null || !current.hasNext()) {
			current = null;
			if (it.hasNext()) {
				current = it.next().iterator();
			} else {
				return false;
			}
		}
		return current.hasNext();
	}
	public StopPoint next() {
		if (current == null || !current.hasNext()) {
			throw new NoSuchElementException();
		}
		return current.next();
	}
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
