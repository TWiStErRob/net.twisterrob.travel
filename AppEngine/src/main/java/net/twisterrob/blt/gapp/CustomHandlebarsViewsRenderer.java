package net.twisterrob.blt.gapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.handlebars.HandlebarsViewsRenderer;
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfiguration;
import jakarta.inject.Singleton;

@Singleton
@Replaces(HandlebarsViewsRenderer.class)
public final class CustomHandlebarsViewsRenderer<T, R> extends HandlebarsViewsRenderer<T, R> {

	public CustomHandlebarsViewsRenderer(
			ViewsConfiguration viewsConfiguration,
			ClassPathResourceLoader resourceLoader,
			HandlebarsViewsRendererConfiguration handlebarsViewsRendererConfiguration,
			Handlebars handlebars
	) {
		super(viewsConfiguration, resourceLoader, handlebarsViewsRendererConfiguration, handlebars);

		this.handlebars.registerHelpers(new HelperSource());
	}

	public static class HelperSource {

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
}
