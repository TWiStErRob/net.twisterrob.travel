package com.twister.london.travel.ui.activity;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.twister.london.travel.R;
import com.twister.london.travel.model.Station;

public class StationAdapter extends BaseListAdapter<Station, StationAdapter.ViewHolder> {

	public StationAdapter(final Context context, final Collection<Station> items) {
		super(context, items, false);
	}

	protected class ViewHolder {
		TextView title;
		TextView description;
	}

	@Override protected int getItemLayoutId() {
		return R.layout.item_station;
	}

	@Override protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (TextView)convertView.findViewById(android.R.id.text1);
		holder.description = (TextView)convertView.findViewById(android.R.id.text2);
		return holder;
	}

	@Override protected void bindView(final ViewHolder holder, final Station currentItem, final View convertView) {
		String title = currentItem.getName();
		String description = String.format("%d: %s", currentItem.getId(), currentItem.getLocation());

		holder.title.setText(title);
		holder.description.setText(description);
	}
}
