package net.twisterrob.blt.model;

import java.util.Locale;

public enum PlatformDirection {
	West() {
		@Override public boolean matches(String input) {
			return input.toLowerCase(Locale.UK).contains("westbound");
		}
	},
	East() {
		@Override public boolean matches(String input) {
			return input.toLowerCase(Locale.UK).contains("eastbound");
		}
	},
	North() {
		@Override public boolean matches(String input) {
			return input.toLowerCase(Locale.UK).contains("northbound");
		}
	},
	South() {
		@Override public boolean matches(String input) {
			return input.toLowerCase(Locale.UK).contains("southbound");
		}
	},
	Other() {
		/**
		 * Matches everything else.
		 * @param input not used
		 */
		@Override public boolean matches(String input) {
			return true;
		}
	}; // not contains the above

	public abstract boolean matches(String input);

	public static PlatformDirection parse(String platformName) {
		for (PlatformDirection dir : values()) {
			if (dir.matches(platformName)) {
				return dir;
			}
		}
		return Other;
	}
}
