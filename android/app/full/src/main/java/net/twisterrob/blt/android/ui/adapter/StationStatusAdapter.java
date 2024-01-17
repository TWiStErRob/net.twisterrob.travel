package net.twisterrob.blt.android.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.app.full.R;
import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData;
import net.twisterrob.blt.model.DelayType;
import net.twisterrob.blt.io.feeds.trackernet.model.LineStatus;
import net.twisterrob.blt.model.LineColors;

public class StationStatusAdapter extends BaseListAdapter<LineStatus, StationStatusAdapter.ViewHolder> {
	private final LineColors colors = new LineColors(App.getInstance().getStaticData().getLineColors());
	private final TrackerNetData trackerNetData = new TrackerNetData();

	public StationStatusAdapter(final Context context, final Collection<LineStatus> lines) {
		super(context, lines, false);
	}

	protected static class ViewHolder {
		TextView line;
		TextView description;
		TextView delay;
	}

	@Override protected int getItemLayoutId() {
		return R.layout.item_line_status;
	}

	@Override protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.line = (TextView)convertView.findViewById(R.id.linestatus_line);
		holder.delay = (TextView)convertView.findViewById(R.id.linestatus_delay);
		holder.description = (TextView)convertView.findViewById(R.id.linestatus_desc);
		return holder;
	}

	@Override protected void bindView(final ViewHolder holder, final LineStatus currentItem, final View convertView) {
		String title = trackerNetData.getDisplayName(currentItem.getLine());
		String description = currentItem.getDescription();
		String delay = trackerNetData.getDisplayName(currentItem.getType());

		holder.line.setText(title);
		holder.delay.setText(delay);
		if (description != null) {
			holder.description.setVisibility(View.VISIBLE);
			holder.description.setText(description);
		} else {
			holder.description.setVisibility(View.GONE);
		}

		holder.line.setTextColor(colors.getForeground(currentItem.getLine()));
		holder.line.setBackgroundColor(colors.getBackground(currentItem.getLine()));

		if (TrackerNetData.DELAY_TYPE_ORDER.compare(currentItem.getType(), DelayType.MinorDelays) >= 0) {
			holder.delay.setTextColor(Color.rgb(0, 25, 168));
		} else {
			holder.delay.setTextColor(Color.rgb(0, 0, 0));
		}
		if (TrackerNetData.DELAY_TYPE_ORDER.compare(currentItem.getType(), DelayType.MinorDelays) > 0) {
			holder.delay.setTypeface(null, Typeface.BOLD);
		} else {
			holder.delay.setTypeface(null, Typeface.NORMAL);
		}
	}
}
