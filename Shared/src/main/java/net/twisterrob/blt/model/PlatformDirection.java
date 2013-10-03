package net.twisterrob.blt.model;
public enum PlatformDirection {
	West() {
		@Override
		public boolean matches(String input) {
			return input.toLowerCase().contains("westbound");
		}
	},
	East() {
		@Override
		public boolean matches(String input) {
			return input.toLowerCase().contains("eastbound");
		}
	},
	North() {
		@Override
		public boolean matches(String input) {
			return input.toLowerCase().contains("northbound");
		}
	},
	South() {
		@Override
		public boolean matches(String input) {
			return input.toLowerCase().contains("southbound");
		}
	},
	Other() {
		@Override
		public boolean matches(String input) {
			return true;
		}
	}; // not contains the above

	public abstract boolean matches(String input);

	public static PlatformDirection parse(String platformName) {
		for (PlatformDirection dir: values()) {
			if (dir.matches(platformName)) {
				return dir;
			}
		}
		return Other;
	}
}