package net.twisterrob.blt.android.ui.adapter;

import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder;
import net.twisterrob.blt.model.*;
import android.content.Context;
import android.content.res.Resources;
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
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final Station currentItem, final View convertView) {
		String title = currentItem.getName();
		String description = String.format("%s, %d: %f, %f", currentItem.getType(), currentItem.getId(), currentItem
				.getLocation().getLatitude(), currentItem.getLocation().getLongitude());
		Drawable icon = bitmapCache.get(currentItem.getType());

		holder.title.setText(title);
		holder.description.setText(description);
		holder.icon.setImageDrawable(icon);
	}
}
