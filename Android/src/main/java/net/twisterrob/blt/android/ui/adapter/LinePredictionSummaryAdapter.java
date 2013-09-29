package net.twisterrob.blt.android.ui.adapter;

import java.util.*;
import java.util.Map.Entry;

import net.twisterrob.blt.android.R;
import net.twisterrob.blt.io.feeds.PredictionSummaryFeed;
import net.twisterrob.blt.model.*;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class LinePredictionSummaryAdapter extends BaseListAdapter<Station, LinePredictionSummaryAdapter.ViewHolder> {
	private static final LineColors colors = new TubeStatusPresentationLineColors();
	private PredictionSummaryFeed m_root;

	public LinePredictionSummaryAdapter(final Context context, final PredictionSummaryFeed root) {
		super(context, root.getStations(), false);
		m_root = root;
	}

	protected class ViewHolder {
		TextView station;
		TextView description;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.item_prediction_summary_station;
	}

	@Override
	protected ViewHolder createHolder(final View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.station = (TextView)convertView.findViewById(R.id.prediction_station_name);
		holder.station.setBackgroundColor(m_root.getLine().getBackground(colors));
		holder.station.setTextColor(m_root.getLine().getForeground(colors));
		holder.description = (TextView)convertView.findViewById(R.id.prediction_station_description);
		return holder;
	}

	@Override
	protected void bindView(final ViewHolder holder, final Station currentItem, final View convertView) {
		String title = String.format("[%s] %s", currentItem.getTrackerNetCode(), currentItem.getName());

		Map<Platform, List<Train>> trains = m_root.collectTrains(currentItem);
		StringBuilder platforms = new StringBuilder();
		for (Entry<Platform, List<Train>> platTrains: trains.entrySet()) {
			String platformDesc = String.format("%s: %d", platTrains.getKey().getName(), platTrains.getValue().size());
			platforms.append(platformDesc).append("\n");
			for (Train train: platTrains.getValue()) {
				String trainDesc = String.format("[%1$tM:%1$tS] %2$s (%3$s)", train.getTimeToStation(),
						train.getDestinationName(), train.getLocation());
				platforms.append("\t").append(trainDesc).append("\n");
			}
		}
		String description = platforms.toString();

		holder.station.setText(title);
		holder.description.setText(description);
	}
}
