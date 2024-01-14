package net.twisterrob.travel.statushistory.view.handlebars

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * @see HandlebarsHelpers
 */
class HandlebarsHelpersUnitTest {

	@Nested
	class formatCssColor {

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
