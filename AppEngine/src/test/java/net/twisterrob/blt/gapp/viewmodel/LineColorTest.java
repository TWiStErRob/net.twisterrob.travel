package net.twisterrob.blt.gapp.viewmodel;

import java.util.*;

import org.junit.*;
import org.mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import net.twisterrob.blt.gapp.viewmodel.LineColor.AllColors;
import net.twisterrob.blt.model.*;

public class LineColorTest {
	@Mock LineColors colors;

	@Before public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test public void testShortColor() {
		testColors(0x000000AB, "#0000AB", 0x000000CD, "#0000CD");
	}
	@Test public void testLongShortColor() {
		testColors(0xFF0000AB, "#0000AB", 0xFF0000CD, "#0000CD");
	}
	@Test public void testLongColor() {
		testColors(0xFFAB00CD, "#AB00CD", 0xFFCD00EF, "#CD00EF");
	}

	private void testColors(int bgColor, String bgExpect, int fgColor, String fgExpect) {
		when(colors.getJubileeBackground()).thenReturn(bgColor);
		when(colors.getJubileeForeground()).thenReturn(fgColor);

		LineColor color = new LineColor(colors, Line.Jubilee);

		assertEquals(Line.Jubilee, color.getLine());
		assertEquals(bgExpect, color.getBackgroundColor());
		assertEquals(fgExpect, color.getForegroundColor());

		verify(colors, Mockito.atLeastOnce()).getJubileeBackground();
		verify(colors, Mockito.atLeastOnce()).getJubileeForeground();
		verifyNoMoreInteractions(colors);
	}

	@Test public void testAllColorsGiveAllLines() {
		AllColors all = new AllColors(colors);
		EnumSet<Line> seen = EnumSet.noneOf(Line.class);
		for (LineColor color : all) {
			if (!seen.add(color.getLine())) {
				fail("Line '" + color.getLine() + "' has been seen already.");
			}
		}
		assertThat(seen, hasSize(Line.values().length));
		verifyZeroInteractions(colors);
	}

	@Test(expected = UnsupportedOperationException.class) public void testAllColorsCannotRemove() {
		new AllColors(colors).iterator().remove();
	}

	@Test public void testAllColorsConsistentProperties() {
		AllColors colors = new AllColors(this.colors);
		Iterator<LineColor> it1 = colors.iterator();
		Iterator<LineColor> getIt1 = colors.getIterator();
		Iterator<LineColor> it2 = colors.iterator();
		Iterator<LineColor> getIt2 = colors.getIterator();
		assertThat(it1, not(equalTo(it2)));
		assertThat(getIt1, not(equalTo(getIt2)));
		assertThat(it1, not(anyOf(equalTo(getIt1), equalTo(getIt2))));
		assertThat(it2, not(anyOf(equalTo(getIt1), equalTo(getIt2))));
	}
}
