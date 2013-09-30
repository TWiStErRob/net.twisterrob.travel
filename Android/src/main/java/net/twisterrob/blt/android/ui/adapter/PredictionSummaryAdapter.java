package net.twisterrob.blt.android.ui.adapter;

import java.util.*;
import java.util.regex.Pattern;

import net.twisterrob.android.adapter.BaseExpandableListAdapter;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter.ChildViewHolder;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter.GroupViewHolder;
import net.twisterrob.blt.io.feeds.PredictionSummaryFeed;
import net.twisterrob.blt.model.*;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class PredictionSummaryAdapter
		extends
			BaseExpandableListAdapter<Station, Platform, GroupViewHolder, ChildViewHolder> {
	private static final LineColors colors = new TubeStatusPresentationLineColors();
	private PredictionSummaryFeed m_root;

	public PredictionSummaryAdapter(final Context context, final PredictionSummaryFeed root) {
		super(context, root.getStations(), root.getStationPlatform());
		m_root = root;
	}
	protected static class GroupViewHolder {
		TextView title;
	}
	protected static class ChildViewHolder {
		TextView platform;
		TextView description;
	}

	@Override
	protected int getGroupLayoutId() {
		return R.layout.item_prediction_summary_station;
	}
	@Override
	protected GroupViewHolder createGroupHolder(View groupConvertView) {
		GroupViewHolder holder = new GroupViewHolder();
		holder.title = (TextView)groupConvertView.findViewById(R.id.prediction_station_name);
		holder.title.setBackgroundColor(m_root.getLine().getBackground(colors));
		holder.title.setTextColor(m_root.getLine().getForeground(colors));
		return holder;
	}

	@Override
	protected void bindGroupView(GroupViewHolder groupHolder, Station currentGroup, List<Platform> currentChildren,
			View groupConvertView) {
		String title = String.format("[%s] %s", currentGroup.getTrackerNetCode(), currentGroup.getName());
		groupHolder.title.setText(title);
	}

	@Override
	protected int getChildLayoutId() {
		return R.layout.item_prediction_summary_platform;
	}
	@Override
	protected ChildViewHolder createChildHolder(View childConvertView) {
		ChildViewHolder holder = new ChildViewHolder();
		holder.platform = (TextView)childConvertView.findViewById(R.id.prediction_platform_name);
		holder.platform.setBackgroundColor(Color.rgb(224, 255, 255));
		holder.description = (TextView)childConvertView.findViewById(R.id.prediction_platform_description);
		return holder;
	}
	@Override
	protected void bindChildView(ChildViewHolder childHolder, Station currentGroup, Platform currentChild,
			View childConvertView) {
		List<Train> trains = m_root.collectTrains(currentGroup, currentChild);

		boolean matches = false;
		for (Direction dir: dirs) {
			matches |= dir.matches(currentChild.getName());
		}

		if (!matches) {
			childHolder.platform.setText("platform filtered out");
			childHolder.platform.setTextColor(Color.rgb(192, 192, 192));
			childHolder.platform.setVisibility(View.VISIBLE);
			childHolder.description.setVisibility(View.GONE);
			return;
		} else {
			childHolder.platform.setTextColor(Color.BLACK);
			childHolder.platform.setVisibility(View.VISIBLE);
			childHolder.description.setVisibility(View.VISIBLE);
		}

		if (trains.size() > 0) {
			String platformDesc = String
					.format("%s: %d trains are approaching.", currentChild.getName(), trains.size());

			StringBuilder trainBuilder = new StringBuilder();
			for (Train train: trains) {
				String trainDesc = String.format("[%1$tM:%1$tS] %2$s (%3$s)", train.getTimeToStation(),
						train.getDestinationName(), train.getLocation());
				trainBuilder.append(trainDesc).append("\n");
			}
			while (trainBuilder.length() > 0 && trainBuilder.charAt(trainBuilder.length() - 1) == '\n') {
				trainBuilder.deleteCharAt(trainBuilder.length() - 1);
			}

			childHolder.platform.setText(platformDesc);
			childHolder.description.setText(trainBuilder);
			childHolder.description.setVisibility(View.VISIBLE);
		} else {
			childHolder.platform.setText(String.format("%s has no trains approaching", currentChild.getName()));
			childHolder.description.setVisibility(View.GONE);
		}
	}

	private final EnumSet<Direction> dirs = EnumSet.allOf(Direction.class);
	public void addDirection(Direction dir) {
		dirs.add(dir);
	}
	public void removeDirection(Direction dir) {
		dirs.remove(dir);
	}

	public static enum Direction {
		West("(?i)westbound"),
		East("(?i)eastbound"),
		North("(?i)northbound"),
		South("(?i)southbound"),
		Other("(?i)^((?!westbound|eastbound|northbound|southbound).)*$"); // not contains the above
		private final Pattern m_pattern;
		private Direction(String pattern) {
			m_pattern = Pattern.compile(pattern);
		}

		public boolean matches(String input) {
			return m_pattern.matcher(input).find();
		}
	}

}
