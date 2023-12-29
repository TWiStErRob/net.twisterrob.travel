package net.twisterrob.travel.statushistory.view.handlebars

import com.github.jknack.handlebars.Options
import java.text.SimpleDateFormat
import java.util.Date
import java.util.EnumMap
import java.util.Locale

@Suppress("unused") // Used by .hbs files.
object HandlebarsHelpers {

	@JvmStatic
	fun assign(varName: String, varValue: Any?, options: Options) =
		options.data(varName, varValue)

	@JvmStatic
	fun add(base: Int, increment: Int): Int =
		base + increment

	@JvmStatic
	fun concat(base: String, extra: String): String =
		base + extra

	@JvmStatic
	fun not(value: Boolean): Boolean =
		!value

	@JvmStatic
	fun or(left: Boolean, right: Boolean): Boolean =
		left || right

	@JvmStatic
	fun and(left: Boolean, right: Boolean): Boolean =
		left && right

	@JvmStatic
	fun empty(value: String?): Boolean =
		value.isNullOrEmpty()

	@JvmStatic
	fun formatDate(value: Date, pattern: String): String =
		SimpleDateFormat(pattern, Locale.getDefault()).format(value)

	/**
	 * Workaround for `myEnumMap.[Foo]` and `lookup myEnumMap Foo` not working.
	 * Usage: Replace `(lookup someEnumMap someEnumKey)` with `(lookupEnumMap someEnumMap someEnumKey)`.
	 * See https://github.com/jknack/handlebars.java/issues/1083.
	 */
	@JvmStatic
	fun <E : Enum<E>> lookupEnumMap(map: EnumMap<E, *>, key: E): Any? =
		map[key]
}