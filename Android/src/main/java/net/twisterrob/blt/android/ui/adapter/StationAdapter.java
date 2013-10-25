package net.twisterrob.blt.android.ui.adapter;

import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.db.model.Station;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder;
import net.twisterrob.blt.model.*;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.*;

public class StationAdapter extends BaseListAdapter<Station, ViewHolder> {
	private Map<StopType, Drawable> bitmapCache = new EnumMap<StopType, Drawable>(StopType.class);
	private Resources m_resources;

	public StationAdapter(final Context context, final Collection<Station> items) {
		super(context, items, false);
		m_resources = context.getResources();
		for (Entry<StopType, Integer> logo: App.getInstance().getStaticData().getStopTypeLogos().entrySet()) {
			bitmapCache.put(logo.getKey(), m_resources.getDrawable(logo.getValue()));
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
		List<Line> lines = App.getInstance().getDataBaseHelper().getLines(currentItem.getId());
		String title = currentItem.getName();
		String description = String.format("%s: %s", currentItem.getType(), lines);
		Drawable icon = bitmapCache.get(currentItem.getType());

		holder.title.setText(title);
		holder.description.setText(description);
		holder.icon.setImageDrawable(icon);
		updateLineColors(holder.lines, lines);
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
}
