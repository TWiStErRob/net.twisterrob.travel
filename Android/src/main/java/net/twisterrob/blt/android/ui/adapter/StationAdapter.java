package net.twisterrob.blt.android.ui.adapter;

import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.*;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.*;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder;
import net.twisterrob.blt.model.*;

public class StationAdapter extends BaseListAdapter<Station, ViewHolder> {
	public StationAdapter(final Context context, final Collection<Station> items) {
		super(context, items, false);
	}
	public static class ViewHolder {
		private final TextView title;
		private final TextView description;
		private final ImageView icon;
		private final View[] lines = new View[6];
		private Context context;

		public ViewHolder(View view) {
			this.context = view.getContext();
			this.title = (TextView)view.findViewById(android.R.id.text1);
			this.description = (TextView)view.findViewById(android.R.id.text2);
			this.icon = (ImageView)view.findViewById(android.R.id.icon);
			lines[0] = view.findViewById(R.id.box_line_1);
			lines[1] = view.findViewById(R.id.box_line_2);
			lines[2] = view.findViewById(R.id.box_line_3);
			lines[3] = view.findViewById(R.id.box_line_4);
			lines[4] = view.findViewById(R.id.box_line_5);
			lines[5] = view.findViewById(R.id.box_line_6);
		}

		public void bind(Station currentItem, String filter) {
			Map<StopType, Integer> logos = App.getInstance().getStaticData().getStopTypeLogos();
			Drawable icon = ContextCompat.getDrawable(context, logos.get(currentItem.getType()));
			CharSequence title = highlight(currentItem.getName(), filter);
			String stationLines = context.getString(R.string.station_lines,
					currentItem.getType(), currentItem.getLines());
			CharSequence description = highlight(stationLines, filter);

			this.title.setText(title);
			this.description.setText(description);
			this.icon.setImageDrawable(icon);
			updateLineColors(currentItem.getLines());
		}

		private CharSequence highlight(String title, String lastFilter) {
			if (lastFilter == null) {
				return title;
			}
			SpannableString text = new SpannableString(title);
			int matchIndex = title.toLowerCase().indexOf(lastFilter.toLowerCase());
			if (0 <= matchIndex) {
				TextAppearanceSpan style = new TextAppearanceSpan(context, R.style.search_highlight);
				text.setSpan(style, matchIndex, matchIndex + lastFilter.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
			return text;
		}

		private void updateLineColors(List<Line> lines) {
			LineColors colors = App.getInstance().getStaticData().getLineColors();
			for (int i = 0; i < this.lines.length; ++i) {
				int bg = Color.TRANSPARENT; // make it invisible by default
				if (i < lines.size()) {
					bg = lines.get(i).getBackground(colors);
				}
				this.lines[i].setBackgroundColor(bg);
			}
		}
	}

	@Override protected int getItemLayoutId() {
		return R.layout.item_station;
	}

	@Override protected ViewHolder createHolder(final View convertView) {
		return new ViewHolder(convertView);
	}

	@Override protected void bindView(final ViewHolder holder, final Station currentItem, final View convertView) {
		holder.bind(currentItem, getLastFilter());
	}

	@Override protected List<Station> filter(List<? extends Station> fullList, String filter, List<Station> result) {
		filter = filter.toLowerCase();
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
		return station.getName().toLowerCase().contains(filter);
	}

	private static boolean matchStopType(Station station, String filter) {
		return station.getType().toString().toLowerCase().contains(filter);
	}

	private static boolean matchLines(Station station, String filter) {
		for (Line line : station.getLines()) {
			if (line.getTitle().toLowerCase().contains(filter)) {
				return true;
			}
		}
		return false;
	}
}
