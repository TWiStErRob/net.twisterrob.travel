package net.twisterrob.travel.statushistory.view.handlebars

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Options
import com.github.jknack.handlebars.TagType
import com.github.jknack.handlebars.Template
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.Calendar

/**
 * @see HandlebarsHelpers
 */
@Suppress("ClassName")
class HandlebarsHelpersUnitTest {

	@Nested
	inner class assign {

		@Test fun `assign value`() {
			val options = options()

			HandlebarsHelpers.assign("myName", "myValue", options)

			val value = Handlebars().compileInline("{{myName}}").apply(options.context)
			assertEquals("myValue", value)
		}

		@Test fun `assign null`() {
			val options = options()

			HandlebarsHelpers.assign("myName", null, options)

			val value = Handlebars().compileInline("{{myName}}").apply(options.context)
			assertEquals("", value)
		}

		@Test fun `clear after assign`() {
			val options = options()

			HandlebarsHelpers.assign("myName", "myValue", options)
			HandlebarsHelpers.assign("myName", null, options)

			val value = Handlebars().compileInline("{{myName}}").apply(options.context)
			assertEquals("", value)
		}

		private fun options(): Options =
			Options
				.Builder(
					Handlebars(),
					"test",
					TagType.SUB_EXPRESSION,
					Context.newContext(null),
					Template.EMPTY
				)
				.build()
	}

	@Nested
	inner class logical {

		@ParameterizedTest
		@CsvSource(
			"true, true, true",
			"true, false, true",
			"false, true, true",
			"false, false, false",
		)
		fun `or truthTable`(left: Boolean, right: Boolean, expected: Boolean) {
			val result = HandlebarsHelpers.or(left, right)

			assertThat(result, equalTo(expected))
		}

		@ParameterizedTest
		@CsvSource(
			"true, true, true",
			"true, false, false",
			"false, true, false",
			"false, false, false",
		)
		fun `and truthTable`(left: Boolean, right: Boolean, expected: Boolean) {
			val result = HandlebarsHelpers.and(left, right)

			assertThat(result, equalTo(expected))
		}

		@ParameterizedTest
		@CsvSource(
			"true, false",
			"false, true",
		)
		fun `not truthTable`(right: Boolean, expected: Boolean) {
			val result = HandlebarsHelpers.not(right)

			assertThat(result, equalTo(expected))
		}
	}

	@Nested
	inner class empty {

		@ParameterizedTest
		@CsvSource(
			"<null>, true",
			"'', true",
			"null, false",
			"' ', false",
			"asdf, false",
			nullValues = ["<null>"],
		)
		fun test(value: String?, expected: Boolean) {
			val result = HandlebarsHelpers.empty(value)

			assertThat(result, equalTo(expected))
		}
	}

	@Nested
	inner class concat {

		@ParameterizedTest
		@CsvSource(
			"'', '', ''",
			"left, '', left",
			"'', right, right",
			"left, right, leftright",
		)
		fun test(left: String, right: String, expected: String) {
			val result = HandlebarsHelpers.concat(left, right)

			assertThat(result, equalTo(expected))
		}
	}

	@Nested
	inner class add {

		@ParameterizedTest
		@CsvSource(
			"0, 0, 0",
			"0, 1, 1",
			"1, 0, 1",
			"1, 1, 2",
			"12, 34, 46",
			"0, -1, -1",
			"-5, -6, -11",
			"3, -5, -2",
		)
		fun test(base: Int, increment: Int, expected: Int) {
			val result = HandlebarsHelpers.add(base, increment)

			assertThat(result, equalTo(expected))
		}
	}

	@Nested
	inner class formatDate {

		@Test fun `format date`() {
			val date = Calendar.getInstance().apply { set(2021, Calendar.AUGUST, 3) }.time

			val result = HandlebarsHelpers.formatDate(date, "yyyy-MM-dd")

			assertEquals("2021-08-03", result)
		}

		@Test fun `invalid format fails`() {
			val date = Calendar.getInstance().apply { set(2021, 8, 3) }.time

			assertThrows<IllegalArgumentException> {
				HandlebarsHelpers.formatDate(date, "invalid")
			}
		}
	}

	@Nested
	inner class formatCssColor {

		@Test fun testShortColor1() {
			testColors(0x000000AB, "#0000AB")
		}

		@Test fun testShortColor2() {
			testColors(0x000000CD, "#0000CD")
		}

		@Test fun testLongShortColor1() {
			testColors(0xFF0000AB.toInt(), "#0000AB")
		}

		@Test fun testLongShortColor2() {
			testColors(0xFF0000CD.toInt(), "#0000CD")
		}

		@Test fun testLongColor1() {
			testColors(0xFFAB00CD.toInt(), "#AB00CD")
		}

		@Test fun testLongColor2() {
			testColors(0xFFCD00EF.toInt(), "#CD00EF")
		}

		@Test fun testEdgeZero() {
			testColors(0x00000000, "#000000")
		}

		@Test fun testEdgeFull() {
			testColors(0xFFFFFFFF.toInt(), "#FFFFFF")
		}

		private fun testColors(colorInput: Int, expected: String) {
			val result = HandlebarsHelpers.formatCssColor(colorInput)

			assertThat(result, equalTo(expected))
		}
	}
}
