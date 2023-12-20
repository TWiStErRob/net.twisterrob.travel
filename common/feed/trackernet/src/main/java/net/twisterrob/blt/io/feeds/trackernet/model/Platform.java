package net.twisterrob.blt.io.feeds.trackernet.model;

import java.util.regex.*;

import net.twisterrob.blt.model.PlatformDirection;

public class Platform {
	private static final Pattern PLATFORM_NUMBER = Pattern.compile(".*platform (\\d+).*", Pattern.CASE_INSENSITIVE);
	private String name;
	private int code;
	private PlatformDirection direction;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		setDirection(PlatformDirection.parse(name));
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}

	public PlatformDirection getDirection() {
		return direction;
	}
	public void setDirection(PlatformDirection direction) {
		this.direction = direction;
	}

	@Override public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null)? 0 : name.hashCode());
		return result;
	}
	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Platform)) {
			return false;
		}
		Platform other = (Platform)obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public int extractPlatformNumber() {
		try {
			Matcher matcher = PLATFORM_NUMBER.matcher(this.getName());
			if (matcher.find()) {
				return Integer.parseInt(matcher.group(1));
			}
		} catch (NumberFormatException ex) {
			// ignore
		}
		return 0;
	}
}
