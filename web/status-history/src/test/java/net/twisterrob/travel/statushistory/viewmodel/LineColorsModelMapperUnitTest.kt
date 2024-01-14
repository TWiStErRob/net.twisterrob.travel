package net.twisterrob.travel.statushistory.viewmodel

import net.twisterrob.blt.model.Line
import net.twisterrob.blt.model.LineColors
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class LineColorsModelMapperUnitTest {

	@Mock lateinit var colors: LineColors

	private lateinit var subject: LineColorsModelMapper

	@BeforeEach fun setUp() {
		subject = LineColorsModelMapper(colors)
	}

	@Test fun testAllColorsGiveAllLines() {
		val allLinesInOrder = @OptIn(ExperimentalStdlibApi::class) Line.entries.sorted()

		val result = subject.map()

		val linesCovered = result.map { it.line }
		assertThat(linesCovered, equalTo(allLinesInOrder))
	}

	@Test fun testJubileeMapping() {
		val fgColor = 0x12345678.toInt()
		val bgColor = 0x87654321.toInt()
		`when`(colors.jubileeBackground).thenReturn(bgColor)
		`when`(colors.jubileeForeground).thenReturn(fgColor)

		val result = subject.map()

		val jubilee = result.single { it.line == Line.Jubilee }
		assertEquals(Line.Jubilee, jubilee.line)
		assertEquals(bgColor, jubilee.backgroundColor)
		assertEquals(fgColor, jubilee.foregroundColor)

		verify(colors).jubileeBackground
		verify(colors).jubileeForeground
	}
}
