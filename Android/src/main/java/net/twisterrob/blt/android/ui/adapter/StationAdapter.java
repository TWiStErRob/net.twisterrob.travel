package net.twisterrob.blt.android.ui.adapter;

import java.io.IOException;
import java.util.*;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.android.utils.tools.IOTools;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.ui.adapter.StationAdapter.ViewHolder;
import net.twisterrob.blt.model.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.*;

public class StationAdapter extends BaseListAdapter<Station, ViewHolder> {
	public StationAdapter(final Context context, final Collection<Station> items) {
		super(context, items, false);
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

	private Map<Type, Bitmap> bitmaps = new EnumMap<Type, Bitmap>(Type.class);
	@Override
	protected void bindView(final ViewHolder holder, final Station currentItem, final View convertView) {
		String title = currentItem.getName();
		String description = String.format("%d: %s", currentItem.getId(), currentItem.getLocation());

		holder.title.setText(title);
		holder.description.setText(description);
		Type type = currentItem.getType();
		Bitmap icon = bitmaps.get(type);
		if (icon == null) {
			try {
				icon = IOTools.getImage(type.getUrl(), true);
				bitmaps.put(type, icon);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		if (icon != null) {
			holder.icon.setImageBitmap(icon);
		}
	}
}
