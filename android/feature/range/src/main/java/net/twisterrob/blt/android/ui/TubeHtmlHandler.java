package net.twisterrob.blt.android.ui;

import java.util.Map;

import org.xml.sax.Attributes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.widget.TextView;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.twisterrob.android.content.HtmlParser;
import net.twisterrob.android.graphics.DrawableBinder;
import net.twisterrob.blt.android.data.AndroidStaticData;
import net.twisterrob.blt.model.Line;
import net.twisterrob.blt.model.LineColors;
import net.twisterrob.blt.model.StopType;
import net.twisterrob.blt.model.TextLineColorScheme;

/**
 * @see <a href="https://chiuki.github.io/advanced-android-textview/#/29">Advanced Android TextView</a>
 * @see <a href="http://flavienlaurent.com/blog/2014/01/31/spans/">Spans, a Powerful Concept.</a>
 */
public class TubeHtmlHandler implements HtmlParser.TagHandler {
	private static final char ICON_PLACEHOLDER = '\uFFFC'; // see android.text.HtmlToSpannedConverter#startImg
	private static final char ICON_SPACING = '\u00A0';
	private static final float ICON_SCALE = 0.75f;

	private final LineColors colors;
	private final Map<StopType, Integer> logos;
	private final Context context;
	private final float textSize;

	public TubeHtmlHandler(Context context, AndroidStaticData staticData) {
		this.context = context;
		this.colors = new LineColors(new TextLineColorScheme(staticData.getLineColors()));
		this.logos = staticData.getStopTypeLogos();
		this.textSize = new TextView(context).getTextSize();
	}

	@Override public boolean handleTag(boolean opening, String tag, Editable output,
			Attributes attributes) {
		if ("ul".equals(tag) && opening) {
			output.append("\n\n");
			return true;
		} else if ("ul".equals(tag) && !opening) {
			output.append("\n");
			return true;
		} else if ("li".equals(tag) && opening) {
			output.append("\t•\u00A0");
			return true;
		} else if ("li".equals(tag) && !opening) {
			output.append("\n");
			return true;
		} else if ("station".equals(tag)) {
			if (opening) {
				Icon icon = Icon.parse(attributes.getValue("icon"));
				Line line = lineFromEnumName(attributes.getValue("line"));
				int pos = output.length();
				output.setSpan(new TubeColorSpanMaker(line, icon), pos, pos, Spannable.SPAN_MARK_MARK);
			} else {
				TubeColorSpanMaker marker = getLast(output, TubeColorSpanMaker.class);
				int start = output.getSpanStart(marker);
				output.removeSpan(marker);

				if (marker != null && start != -1) {
					int end = output.length();
					Line line = marker.line;
					Icon icon = marker.icon;
					applySpan(output, start, end, line, icon);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public void applySpan(Editable output, int start, int end, Line line, Icon icon) {
		if (icon != Icon.None) {
			@DrawableRes int logoID = logos.get(line.getDefaultStopType());
			Drawable logo = ContextCompat.getDrawable(context, logoID);
			int iconSize = (int)(textSize * ICON_SCALE);
			DrawableBinder.bindTopLeft(logo, iconSize, iconSize);
			ImageSpan image = new ImageSpan(logo, DynamicDrawableSpan.ALIGN_BASELINE);
			switch (icon) {
				case Before:
					String imagePlaceholder = "" + ICON_PLACEHOLDER + ICON_SPACING;
					output.insert(start, imagePlaceholder);
					output.setSpan(image, start, start + 1, SPAN_EXCLUSIVE_EXCLUSIVE);
					start += imagePlaceholder.length();
					break;
				case After:
					output.append(ICON_SPACING).append(ICON_PLACEHOLDER);
					output.setSpan(image, end + 1, end + 2, SPAN_EXCLUSIVE_EXCLUSIVE);
					break;
			}
		}
		@ColorInt int fgColor = colors.getForeground(line);
		@ColorInt int bgColor = colors.getBackground(line);
		if (start != end) { // there's some text
			output.setSpan(new ForegroundColorSpan(fgColor), start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
			output.setSpan(new BackgroundColorSpan(bgColor), start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}

	@SuppressWarnings("unchecked")
	// modified helper from android.text.HtmlToSpannedConverter (jump to source of Html class and it's below)
	private static <T> T getLast(Editable text, Class<T> kind) {
		Object[] objs = text.getSpans(0, text.length(), kind);
		for (int i = objs.length - 1; i >= 0; --i) {
			if (text.getSpanFlags(objs[i]) == Spannable.SPAN_MARK_MARK) {
				return (T)objs[i];
			}
		}
		return null;
	}

	private static @NonNull Line lineFromEnumName(@Nullable String line) {
		try {
			return Line.valueOf(line);
		} catch (IllegalArgumentException ex) {
			return Line.unknown;
		}
	}

	private static class TubeColorSpanMaker {
		private final Line line;
		private final Icon icon;
		private TubeColorSpanMaker(Line line, Icon icon) {
			this.line = line;
			this.icon = icon;
		}
	}

	public enum Icon {
		None,
		Before,
		After;

		private static @NonNull Icon parse(String icon) {
			if ("none".equals(icon)) {
				return None;
			} else if ("before".equals(icon)) {
				return Before;
			} else if ("after".equals(icon)) {
				return After;
			} else if (Boolean.parseBoolean(icon)) {
				return Before;
			} else {
				return After;
			}
		}
	}
}
