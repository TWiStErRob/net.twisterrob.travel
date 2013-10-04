package net.twisterrob.blt.android.ui.adapter;

import java.util.*;

import net.twisterrob.android.adapter.BaseFilteringExpandableListAdapter;
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
			BaseFilteringExpandableListAdapter<Station, Platform, GroupViewHolder, ChildViewHolder> {
	private static final LineColors colors = new TubeStatusPresentationLineColors();
	private PredictionSummaryFeed m_root;

	public PredictionSummaryAdapter(final Context context, final PredictionSummaryFeed root) {
		this(context, root, EnumSet.allOf(PlatformDirection.class));
	}
	public PredictionSummaryAdapter(final Context context, final PredictionSummaryFeed root,
			Collection<PlatformDirection> directionsEnabled) {
		super(context, root.getStations(), root.getStationPlatform());
		m_root = root;
		m_directions.addAll(directionsEnabled);
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
		GroupViewHolder groupHolder = new GroupViewHolder();
		groupHolder.title = (TextView)groupConvertView.findViewById(R.id.prediction_station_name);
		groupHolder.title.setBackgroundColor(m_root.getLine().getBackground(colors));
		groupHolder.title.setTextColor(m_root.getLine().getForeground(colors));
		return groupHolder;
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
		ChildViewHolder childHolder = new ChildViewHolder();
		childHolder.platform = (TextView)childConvertView.findViewById(R.id.prediction_platform_name);
		childHolder.platform.setBackgroundColor(Color.rgb(224, 255, 255));
		childHolder.description = (TextView)childConvertView.findViewById(R.id.prediction_platform_description);
		return childHolder;
	}
	@Override
	protected void bindChildView(ChildViewHolder childHolder, Station currentGroup, Platform currentChild,
			View childConvertView) {
		List<Train> trains = m_root.collectTrains(currentGroup, currentChild);

		if (trains.size() > 0) {
			String platformDesc = String
					.format("%s: %d trains are approaching.", currentChild.getName(), trains.size());
			String trainBuilder = buildIncomingTrains(trains);

			childHolder.platform.setText(platformDesc);
			childHolder.description.setText(trainBuilder);
			childHolder.description.setVisibility(View.VISIBLE);
		} else {
			childHolder.platform.setText(String.format("%s has no trains approaching", currentChild.getName()));
			childHolder.description.setVisibility(View.GONE);
		}
	}

	protected String buildIncomingTrains(List<Train> trains) {
		StringBuilder trainBuilder = new StringBuilder();
		for (Train train: trains) {
			String trainDesc = String.format("[%1$tM:%1$tS] %2$s (%3$s)", train.getTimeToStation(),
					train.getDestinationName(), train.getLocation());
			trainBuilder.append(trainDesc).append("\n");
		}
		// remove trailing newlines
		while (trainBuilder.length() > 0 && trainBuilder.charAt(trainBuilder.length() - 1) == '\n') {
			trainBuilder.deleteCharAt(trainBuilder.length() - 1);
		}
		return trainBuilder.toString();
	}

	private Set<PlatformDirection> m_directions = EnumSet.noneOf(PlatformDirection.class);

	public void addDirection(PlatformDirection dir) {
		m_directions.add(dir);
	}
	public void removeDirection(PlatformDirection dir) {
		m_directions.remove(dir);
	}

	@Override
	protected List<Station> filterGroups(List<Station> groups) {
		List<Station> filtered = new ArrayList<Station>();
		for (Station station: groups) {
			if (matches(getChildren(station))) {
				filtered.add(station);
			}
		}
		return filtered;
	}

	@Override
	protected List<Platform> filterChildren(List<Platform> children, Station group) {
		List<Platform> filtered = new ArrayList<Platform>();
		for (Platform platform: children) {
			if (matches(platform)) {
				filtered.add(platform);
			}
		}
		return filtered;
	}

	protected boolean matches(Platform currentChild) {
		return m_directions.contains(currentChild.getDirection());
	}

	protected boolean matches(List<Platform> currentChildren) {
		boolean matches = false;
		for (Platform platform: currentChildren) {
			matches = matches || matches(platform);
		}
		return matches;
	}
}
