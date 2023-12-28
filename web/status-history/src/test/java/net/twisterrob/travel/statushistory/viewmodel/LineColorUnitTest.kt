package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineColors
import net.twisterrob.travel.statushistory.viewmodel.LineColor.AllColors
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.anyOf
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class LineColorUnitTest {

	@get:Rule val mockito: MockitoRule = MockitoJUnit.rule()

	@Mock lateinit var colors: LineColors

	@Test fun testShortColor() {
		testColors(0x000000AB, "#0000AB", 0x000000CD, "#0000CD")
	}

	@Test fun testLongShortColor() {
		testColors(0xFF0000AB.toInt(), "#0000AB", 0xFF0000CD.toInt(), "#0000CD")
	}

	@Test fun testLongColor() {
		testColors(0xFFAB00CD.toInt(), "#AB00CD", 0xFFCD00EF.toInt(), "#CD00EF")
	}

	private fun testColors(bgColor: Int, bgExpect: String, fgColor: Int, fgExpect: String) {
		`when`(colors.jubileeBackground).thenReturn(bgColor)
		`when`(colors.jubileeForeground).thenReturn(fgColor)

		val color = LineColor(colors, Line.Jubilee)

		assertEquals(Line.Jubilee, color.line)
		assertEquals(bgExpect, color.backgroundColor)
		assertEquals(fgExpect, color.foregroundColor)

		verify(colors, atLeastOnce()).jubileeBackground
		verify(colors, atLeastOnce()).jubileeForeground
		verifyNoMoreInteractions(colors)
	}

	@Test fun testAllColorsGiveAllLines() {
		val all = AllColors(colors)
		assertThat(all.map { it.line }, equalTo(@OptIn(ExperimentalStdlibApi::class) Line.entries.sorted()))
		verifyNoInteractions(colors)
	}

	@Test fun testAllColorsHasValidIterator() {
		val all = AllColors(colors)

		val iterator = all.iterator()
		while (iterator.hasNext()) {
			iterator.next()
		}

		assertThrows(NoSuchElementException::class.java) { iterator.next() }
	}

	@Test fun testAllColorsConsistentProperties() {
		val colors = AllColors(colors)
		val it1 = colors.iterator()
		val getIt1 = colors.iterator()
		val it2 = colors.iterator()
		val getIt2 = colors.iterator()
		assertThat(it1, not(equalTo(it2)))
		assertThat(getIt1, not(equalTo(getIt2)))
		assertThat(it1, not(anyOf(equalTo(getIt1), equalTo(getIt2))))
		assertThat(it2, not(anyOf(equalTo(getIt1), equalTo(getIt2))))
	}
}
