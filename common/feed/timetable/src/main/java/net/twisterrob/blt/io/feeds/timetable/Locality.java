package net.twisterrob.blt.io.feeds.timetable;

import java.util.Locale;

public class Locality {
	private String id;
	private String name;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override public String toString() {
		return String.format(Locale.ROOT, "%2$s {%1$s}", id, name);
	}
}
