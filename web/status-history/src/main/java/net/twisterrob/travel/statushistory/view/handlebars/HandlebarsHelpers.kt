package net.twisterrob.travel.statushistory.view.handlebars;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;

import com.github.jknack.handlebars.Options;

public class HandlebarsHelpers {

	public static void assign(String varName, Object varValue, Options options) {
		options.data(varName, varValue);
	}

	public static int add(int base, int increment) {
		return base + increment;
	}

	public static String concat(String base, String extra) {
		return base + extra;
	}

	public static boolean not(boolean value) {
		return !value;
	}

	public static boolean or(boolean left, boolean right) {
		return left || right;
	}

	public static boolean and(boolean left, boolean right) {
		return left && right;
	}

	public static boolean empty(String value) {
		return value == null || value.isEmpty();
	}

	public static String formatDate(Date value, String pattern) {
		return new SimpleDateFormat(pattern, Locale.getDefault()).format(value);
	}

	/**
	 * Workaround for `myEnumMap.[Foo]` and `lookup myEnumMap Foo` not working.
	 * Usage: Replace {@code (lookup someEnumMap someEnumKey)} with {@code (lookupEnumMap someEnumMap someEnumKey)}.
	 * @see https://github.com/jknack/handlebars.java/issues/1083
	 */
	public static <E extends Enum<E>> Object lookupEnumMap(EnumMap<E, ?> map, E key) {
		return map.get(key);
	}
}
