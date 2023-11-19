package net.twisterrob.blt.android.ui.adapter;

import java.util.Collection;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.widget.TextView;

import net.twisterrob.android.adapter.BaseListAdapter;
import net.twisterrob.blt.android.*;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.io.feeds.trackernet.model.*;
import net.twisterrob.blt.model.LineColors;

public class StationStatusAdapter extends BaseListAdapter<LineStatus, StationStatusAdapter.ViewHolder> {
	private final LineColors colors = App.getInstance().getStaticData().getLineColors();

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
		String title = currentItem.getLine().getTitle();
		String description = currentItem.getDescription();
		String delay = currentItem.getType().getTitle();

		holder.line.setText(title);
		holder.delay.setText(delay);
		if (description != null) {
			holder.description.setVisibility(View.VISIBLE);
			holder.description.setText(description);
		} else {
			holder.description.setVisibility(View.GONE);
		}

		holder.line.setTextColor(currentItem.getLine().getForeground(colors));
		holder.line.setBackgroundColor(currentItem.getLine().getBackground(colors));

		if (DelayType.ORDER_SEVERITY.compare(currentItem.getType(), DelayType.MinorDelays) >= 0) {
			holder.delay.setTextColor(Color.rgb(0, 25, 168));
		} else {
			holder.delay.setTextColor(Color.rgb(0, 0, 0));
		}
		if (DelayType.ORDER_SEVERITY.compare(currentItem.getType(), DelayType.MinorDelays) > 0) {
			holder.delay.setTypeface(null, Typeface.BOLD);
		} else {
			holder.delay.setTypeface(null, Typeface.NORMAL);
		}
	}
}
