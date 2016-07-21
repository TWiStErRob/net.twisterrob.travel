package net.twisterrob.blt.android.ui;

import java.util.Map;

import org.xml.sax.Attributes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.*;
import android.support.v4.content.ContextCompat;
import android.text.*;
import android.text.style.*;

import static android.text.Spanned.*;

import net.twisterrob.android.content.HtmlParser;
import net.twisterrob.android.graphics.DrawableBinder;
import net.twisterrob.android.view.TextAppearanceAccessor;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.model.*;

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
	public TubeHtmlHandler(Context context) {
		this.context = context;
		this.colors = new TextLineColors(App.getInstance().getStaticData().getLineColors());
		this.logos = App.getInstance().getStaticData().getStopTypeLogos();
		this.textSize = TextAppearanceAccessor.getDefaultTextSize(context);
	}
	@Override public boolean handleTag(boolean opening, String tag, Editable output, Attributes attributes) {
		if (!"station".equals(tag)) {
			if (tag.equals("ul") && !opening) {
				output.append("\n");
			}
			if (tag.equals("li") && opening) {
				output.append("\n\t• ");
			}
			return false;
		}

		if (opening) {
			Icon icon = Icon.parse(attributes.getValue("icon"));
			Line line = Line.fromEnumName(attributes.getValue("line"));
			int pos = output.length();
			output.setSpan(new TubeColorSpanMaker(line, icon), pos, pos, Spannable.SPAN_MARK_MARK);
		} else {
			TubeColorSpanMaker marker = getLast(output, TubeColorSpanMaker.class);
			int start = output.getSpanStart(marker);
			output.removeSpan(marker);

			if (marker != null && start != -1) {
				int end = output.length();
				if (marker.icon != Icon.None) {
					@DrawableRes int logoID = logos.get(marker.line.getDefaultStopType());
					Drawable logo = ContextCompat.getDrawable(context, logoID);
					int iconSize = (int)(textSize * ICON_SCALE);
					DrawableBinder.bindTopLeft(logo, iconSize, iconSize);
					ImageSpan image = new ImageSpan(logo, DynamicDrawableSpan.ALIGN_BASELINE);
					switch (marker.icon) {
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
				@ColorInt int fgColor = marker.line.getForeground(colors);
				@ColorInt int bgColor = marker.line.getBackground(colors);
				if (start != end) { // there's some text
					output.setSpan(new ForegroundColorSpan(fgColor), start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
					output.setSpan(new BackgroundColorSpan(bgColor), start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return true;
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

	private static class TubeColorSpanMaker {
		private final Line line;
		private final Icon icon;
		private TubeColorSpanMaker(Line line, Icon icon) {
			this.line = line;
			this.icon = icon;
		}
	}

	private enum Icon {
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
