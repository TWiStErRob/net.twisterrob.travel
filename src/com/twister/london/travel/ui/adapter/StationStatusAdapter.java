package com.twister.london.travel.ui.adapter;

import java.util.*;

import android.content.Context;
import android.view.View;
import android.widget.*;

import com.twister.london.travel.R;
import com.twister.london.travel.model.*;

public class StationStatusAdapter extends BaseListAdapter<Map.Entry<Line, LineStatus>, StationStatusAdapter.ViewHolder> {
	private static final LineColors colors = new TFLColourStandard3ScreenLineColors();

	public StationStatusAdapter(final Context context, final Collection<Map.Entry<Line, LineStatus>> items) {
		super(context, items, false);
	}

	protected class ViewHolder {
		TextView title;
		TextView description;
		ImageView icon;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_line_status;
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
	protected void bindView(final ViewHolder holder, final Map.Entry<Line, LineStatus> currentItem,
			final View convertView) {
		String title = currentItem.getKey().getName();
		holder.title.setTextColor(currentItem.getKey().getLine().getForeground(colors));
		holder.title.setBackgroundColor(currentItem.getKey().getLine().getBackground(colors));
		LineStatus lineStatus = currentItem.getValue();
		String description = String.format("[%s] %s", lineStatus.getId(), lineStatus.getDescription());

		holder.title.setText(title);
		holder.description.setText(description);
		//		new ImageViewDownloader(holder.icon, new Callback<ImageView>() {
		//			@Override
		//			public void call(ImageView param) {
		//				// fix width to be square
		//				LayoutParams params = param.getLayoutParams();
		//				params.width = param.getHeight();
		//				param.setLayoutParams(params);
		//			}
		//		}).execute(currentItem.getType().getUrl());
	}
}
