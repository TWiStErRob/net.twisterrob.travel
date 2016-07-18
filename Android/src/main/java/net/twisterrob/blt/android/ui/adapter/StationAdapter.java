package net.twisterrob.blt.android.ui.adapter;

import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder;
import net.twisterrob.blt.model.*;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.*;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.*;
import android.widget.TextView.BufferType;

public class StationAdapter extends BaseListAdapter<Station, ViewHolder> {
	private Map<StopType, Drawable> bitmapCache = new EnumMap<StopType, Drawable>(StopType.class);

	public StationAdapter(final Context context, final Collection<Station> items) {
		super(context, items, false);
		for (Entry<StopType, Integer> logo: App.getInstance().getStaticData().getStopTypeLogos().entrySet()) {
			bitmapCache.put(logo.getKey(), ContextCompat.getDrawable(context, logo.getValue()));
		}
	}
	protected static class ViewHolder {
		TextView title;
		TextView description;
		ImageView icon;
		View[] lines = new View[6];
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_station;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView)convertView.findViewById(android.R.id.text1);
		holder.description = (TextView)convertView.findViewById(android.R.id.text2);
		holder.icon = (ImageView)convertView.findViewById(android.R.id.icon);
		holder.lines[0] = convertView.findViewById(R.id.box_line_1);
		holder.lines[1] = convertView.findViewById(R.id.box_line_2);
		holder.lines[2] = convertView.findViewById(R.id.box_line_3);
		holder.lines[3] = convertView.findViewById(R.id.box_line_4);
		holder.lines[4] = convertView.findViewById(R.id.box_line_5);
		holder.lines[5] = convertView.findViewById(R.id.box_line_6);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final Station currentItem, final View convertView) {
		Drawable icon = bitmapCache.get(currentItem.getType());
		SpannableString title = highlight(currentItem.getName());
		SpannableString description = highlight(String.format("%s: %s", currentItem.getType(), currentItem.getLines()));

		holder.title.setText(title, BufferType.SPANNABLE);
		holder.description.setText(description, BufferType.SPANNABLE);
		holder.icon.setImageDrawable(icon);
		updateLineColors(holder.lines, currentItem.getLines());
	}

	private SpannableString highlight(String title) {
		SpannableString text = new SpannableString(title);
		String lastFilter = getLastFilter();
		if (lastFilter != null) {
			int matchIndex = title.toLowerCase().indexOf(lastFilter.toLowerCase());
			if (0 <= matchIndex) {
				TextAppearanceSpan style = new TextAppearanceSpan(m_context, R.style.search_highlight);
				text.setSpan(style, matchIndex, matchIndex + lastFilter.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
		}
		return text;
	}

	private static void updateLineColors(View[] views, List<Line> lines) {
		LineColors colors = App.getInstance().getStaticData().getLineColors();
		for (int i = 0; i < views.length; ++i) {
			int bg = Color.TRANSPARENT;
			if (i < lines.size()) {
				bg = lines.get(i).getBackground(colors);
			}
			views[i].setBackgroundColor(bg);
		}
	}

	@Override
	protected List<Station> filter(List<? extends Station> fullList, String filter, List<Station> resultList) {
		filter = filter.toLowerCase();
		List<Station> nameMatches = new LinkedList<Station>();
		List<Station> typeMatches = new LinkedList<Station>();
		List<Station> lineMatches = new LinkedList<Station>();
		for (Station station: fullList) {
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
		resultList.addAll(nameMatches);
		resultList.addAll(typeMatches);
		resultList.addAll(lineMatches);
		return resultList;
	}

	private static boolean matchName(Station station, String filter) {
		return station.getName().toLowerCase().contains(filter);
	}

	private static boolean matchStopType(Station station, String filter) {
		return station.getType().toString().toLowerCase().contains(filter);
	}

	private static boolean matchLines(Station station, String filter) {
		for (Line line: station.getLines()) {
			if (line.getTitle().toLowerCase().contains(filter)) {
				return true;
			}
		}
		return false;
	}
}
