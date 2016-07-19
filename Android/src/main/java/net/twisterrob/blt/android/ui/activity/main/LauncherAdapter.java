package net.twisterrob.blt.android.ui.activity.main;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.ui.activity.main.LauncherAdapter.LauncherViewHolder;

class LauncherAdapter extends BaseListAdapter<LauncherItem, LauncherViewHolder> {
	protected LauncherAdapter(Context context, Collection<LauncherItem> items) {
		super(context, items);
	}

	public static class LauncherViewHolder {
		protected TextView title;
	}

	@Override protected int getItemLayoutId() {
		return R.layout.item_main_launcher;
	}
	@Override protected LauncherViewHolder createHolder(View convertView) {
		LauncherAdapter.LauncherViewHolder holder = new LauncherViewHolder();
		holder.title = (TextView)convertView.findViewById(R.id.main_launcher_title);
		return holder;
	}
	@Override protected void bindView(LauncherViewHolder holder, LauncherItem currentItem, View convertView) {
		holder.title.setText(currentItem.getTitle());
	}
}
