package net.twisterrob.blt.gapp.viewmodel;

import java.util.*;

import net.twisterrob.blt.model.*;

public class LineColor {
	private final LineColors colors;
	private final Line line;
	public LineColor(LineColors colors, Line line) {
		this.colors = colors;
		this.line = line;
	}

	public Line getLine() {
		return line;
	}

	public String getForegroundColor() {
		return colorString(line.getForeground(colors));
	}
	public String getBackgroundColor() {
		return colorString(line.getBackground(colors));
	}
	private static String colorString(int color) {
		return String.format(Locale.ROOT, "#%06X", color & 0xFFFFFF);
	}

	public static class AllColors implements Iterable<LineColor> {
		private final LineColors colors;
		public AllColors(LineColors colors) {
			this.colors = colors;
		}

		@Override public Iterator<LineColor> iterator() {
			return new Iterator<LineColor>() {
				private final Line[] lines = Line.values();
				private int current = 0;
				@Override public boolean hasNext() {
					return current < lines.length;
				}
				@Override public LineColor next() {
					return new LineColor(colors, lines[current++]);
				}
				@Override public void remove() {
					throw new UnsupportedOperationException("remove");
				}
			};
		}

		/**
		 * A method that can be called in EL, because they forgot to include Iterable in the JSTL standard.
		 * @see <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=44284">WontFix bug for Iterable support</a>
		 */
		public Iterator<LineColor> getIterator() {
			return iterator();
		}
	}
}
