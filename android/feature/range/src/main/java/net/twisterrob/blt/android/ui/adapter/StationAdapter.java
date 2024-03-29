package net.twisterrob.blt.android.ui.adapter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.data.AndroidStaticData;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.android.feature.range.R;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder.DescriptionFormatter;
import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData;
import net.twisterrob.blt.model.Line;
import net.twisterrob.blt.model.LineColors;
import net.twisterrob.blt.model.StopType;

public class StationAdapter extends BaseListAdapter<Station, ViewHolder> {

	private final TrackerNetData trackerNetData = new TrackerNetData();
	private final AndroidStaticData staticData;

	public StationAdapter(final Context context, final Collection<Station> items, AndroidStaticData staticData) {
		super(context, items, false);
		this.staticData = staticData;
	}
	public static class ViewHolder {
		private final TextView title;
		private final TextView description;
		private final ImageView icon;
		private final DescriptionFormatter descriptionFormatter;
		private final View[] lines = new View[6];
		private final Context context;

		private final AndroidStaticData staticData;
		private final TrackerNetData trackerNetData = new TrackerNetData();

		public interface DescriptionFormatter {
			CharSequence format(Station station);
		}

		public ViewHolder(final View view, AndroidStaticData staticData, DescriptionFormatter descriptionFormatter) {
			this.context = view.getContext();
			this.descriptionFormatter = descriptionFormatter;
			this.title = (TextView)view.findViewById(android.R.id.text1);
			this.description = (TextView)view.findViewById(android.R.id.text2);
			this.icon = (ImageView)view.findViewById(android.R.id.icon);
			this.staticData = staticData;
			lines[0] = view.findViewById(R.id.box_line_1);
			lines[1] = view.findViewById(R.id.box_line_2);
			lines[2] = view.findViewById(R.id.box_line_3);
			lines[3] = view.findViewById(R.id.box_line_4);
			lines[4] = view.findViewById(R.id.box_line_5);
			lines[5] = view.findViewById(R.id.box_line_6);
			OnClickListener click = new OnClickListener() {
				@Override public void onClick(View v) {
					SpannableStringBuilder message = new SpannableStringBuilder("Lines: ");
					final int initialLength = message.length();
//					TubeHtmlHandler html = new TubeHtmlHandler(v.getContext());
					for (int i = lines.length - 1; i >= 0; i--) {
						Line line = (Line)lines[i].getTag();
						if (line != null) {
							if (initialLength != message.length()) {
								message.append(", ");
							}
							String current = trackerNetData.getDisplayName(line);
//							int start = message.length();
							message.append(current);
							// disabled, because can't guarantee toast will have bright BG
//							html.applySpan(message, start, message.length(), line, Icon.After);
						}
					}
					Toast.makeText(v.getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
				}
			};
			for (View lineView : lines) {
				lineView.setOnClickListener(click);
			}
		}

		public void bind(Station currentItem, String filter) {
			Map<StopType, Integer> logos = staticData.getStopTypeLogos();
			Drawable icon = ContextCompat.getDrawable(context, logos.get(currentItem.getType()));
			CharSequence title = highlight(currentItem.getName(), filter);
			CharSequence stationLines = descriptionFormatter.format(currentItem);
			CharSequence description = highlight(stationLines, filter);

			this.title.setText(title);
			this.description.setText(description);
			this.icon.setImageDrawable(icon);
			updateLineColors(currentItem.getLines());
		}

		private CharSequence highlight(CharSequence title, String lastFilter) {
			if (lastFilter == null || lastFilter.length() == 0) {
				return title;
			}
			SpannableString text = new SpannableString(title);
			String haystack = title.toString().toLowerCase(Locale.UK);
			String needle = lastFilter.toLowerCase(Locale.UK);
			int matchIndex = haystack.indexOf(needle);
			while (0 <= matchIndex) {
				TextAppearanceSpan style = new TextAppearanceSpan(context, R.style.search_highlight);
				text.setSpan(style, matchIndex, matchIndex + lastFilter.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				matchIndex = haystack.indexOf(needle, matchIndex + needle.length());
			}
			return text;
		}

		private void updateLineColors(List<Line> lines) {
			LineColors colors = new LineColors(staticData.getLineColors());
			for (int i = 0; i < this.lines.length; ++i) {
				View lineView = this.lines[i];
				if (i < lines.size()) {
					Line line = lines.get(i);
					lineView.setVisibility(View.VISIBLE);
					lineView.setBackgroundColor(colors.getBackground(line));
					lineView.setContentDescription(trackerNetData.getDisplayName(line));
					lineView.setTag(line);
				} else {
					lineView.setVisibility(View.INVISIBLE);
					lineView.setBackgroundColor(Color.TRANSPARENT);
					lineView.setContentDescription(null);
					lineView.setTag(null);
				}
			}
		}
	}

	@Override protected int getItemLayoutId() {
		return R.layout.item_station;
	}

	@Override protected ViewHolder createHolder(final View convertView) {
		return new ViewHolder(convertView, staticData, new DescriptionFormatter() {
			@Override public CharSequence format(Station station) {
				return convertView.getContext().getString(R.string.station_lines,
						station.getType(), station.getLines());
			}
		});
	}

	@Override protected void bindView(final ViewHolder holder, final Station currentItem, final View convertView) {
		holder.bind(currentItem, getLastFilter());
	}

	@Override protected List<Station> filter(List<? extends Station> fullList, String filter, List<Station> result) {
		filter = filter.toLowerCase(Locale.UK);
		List<Station> nameMatches = new LinkedList<>();
		List<Station> typeMatches = new LinkedList<>();
		List<Station> lineMatches = new LinkedList<>();
		for (Station station : fullList) {
			if (matchName(station, filter)) {
				nameMatches.add(station);
			} else if (matchStopType(station, filter)) {
				typeMatches.add(station);
			} else if (matchLines(station, filter)) {
				lineMatches.add(station);
			} else {
				// no match
			}
		}
		result.addAll(nameMatches);
		result.addAll(typeMatches);
		result.addAll(lineMatches);
		return result;
	}

	private static boolean matchName(Station station, String filter) {
		return station.getName().toLowerCase(Locale.UK).contains(filter);
	}

	private static boolean matchStopType(Station station, String filter) {
		return station.getType().toString().toLowerCase(Locale.UK).contains(filter);
	}

	private boolean matchLines(Station station, String filter) {
		for (Line line : station.getLines()) {
			if (trackerNetData.getDisplayName(line).toLowerCase(Locale.UK).contains(filter)) {
				return true;
			}
		}
		return false;
	}
}
