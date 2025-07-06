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

	@Mock(strictness = Mock.Strictness.LENIENT)
	lateinit var colors: LineColors

	private lateinit var subject: LineColorsModelMapper

	@BeforeEach fun setUp() {
		subject = LineColorsModelMapper(colors)
	}

	@Test fun testAllColorsGiveAllLines() {
		val allLinesInOrder = Line.entries.sorted().map { it.name }

		val result = subject.map()

		val linesCovered = result.map { it.lineId }
		assertThat(linesCovered, equalTo(allLinesInOrder))
	}

	@Test fun testJubileeMapping() {
		val fgColor = 0x12345678
		val bgColor = 0x87654321.toInt()
		`when`(colors.getBackground(Line.Jubilee)).thenReturn(bgColor)
		`when`(colors.getForeground(Line.Jubilee)).thenReturn(fgColor)

		val result = subject.map()

		val jubilee = result.single { it.lineId == Line.Jubilee.name }
		assertEquals(Line.Jubilee.name, jubilee.lineId)
		assertEquals(bgColor, jubilee.backgroundColor)
		assertEquals(fgColor, jubilee.foregroundColor)

		verify(colors).getBackground(Line.Jubilee)
		verify(colors).getForeground(Line.Jubilee)
	}
}
