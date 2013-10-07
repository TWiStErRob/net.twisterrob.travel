package net.twisterrob.blt.android.ui.adapter;

import java.util.*;

import net.twisterrob.android.adapter.BaseFilteringExpandableList3Adapter;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter.ChildViewHolder;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter.GroupViewHolder;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter.TrainViewHolder;
import net.twisterrob.blt.io.feeds.PredictionSummaryFeed;
import net.twisterrob.blt.model.*;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.*;

public class PredictionSummaryAdapter
		extends
			BaseFilteringExpandableList3Adapter<Station, Platform, Train, GroupViewHolder, ChildViewHolder, TrainViewHolder> {
	private static final LineColors colors = new TubeStatusPresentationLineColors();
	private PredictionSummaryFeed m_root;

	public PredictionSummaryAdapter(final Context context, ExpandableListView outerList,
			final PredictionSummaryFeed root, Collection<PlatformDirection> directionsEnabled) {
		super(context, outerList, map(root));
		m_root = root;
		m_directions.addAll(directionsEnabled);
	}

	private static Map<Station, Map<Platform, List<Train>>> map(PredictionSummaryFeed root) {
		Map<Station, Map<Platform, List<Train>>> data = new HashMap<Station, Map<Platform, List<Train>>>();
		for (Station station: root.getStationPlatform().keySet()) {
			data.put(station, root.collectTrains(station));
		}
		return data;
	}

	protected static class GroupViewHolder {
		TextView title;
	}
	protected static class ChildViewHolder {
		TextView platform;
		TextView description;
	}
	protected static class TrainViewHolder {
		TextView train;
		TextView description;
	}

	@Override
	protected int getLevel1LayoutId() {
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
	protected void bindLevel1View(GroupViewHolder level1Holder, Station currentLevel1, List<Platform> currentLevel2,
			View level1ConvertView) {
		String title = String.format("[%s] %s", currentLevel1.getTrackerNetCode(), currentLevel1.getName());
		level1Holder.title.setText(title);
	}

	@Override
	protected int getLevel2LayoutId() {
		return R.layout.item_prediction_summary_platform;
	}
	@Override
	protected ChildViewHolder createLevel2Holder(View level2ConvertView) {
		ChildViewHolder childHolder = new ChildViewHolder();
		childHolder.platform = (TextView)level2ConvertView.findViewById(R.id.prediction_platform_name);
		childHolder.platform.setBackgroundColor(Color.rgb(224, 255, 255));
		childHolder.description = (TextView)level2ConvertView.findViewById(R.id.prediction_platform_description);
		return childHolder;
	}
	@Override
	protected void bindLevel2View(ChildViewHolder level2Holder, Station currentLevel1, Platform currentLevel2,
			List<Train> currentLevel3, View level2ConvertView) {
		if (currentLevel3.size() > 0) {
			String platformDesc = String.format("%s: %d trains are approaching.", currentLevel2.getName(),
					currentLevel3.size());
			String trainBuilder = buildIncomingTrains(currentLevel3);

			level2Holder.platform.setText(platformDesc);
			level2Holder.description.setText(trainBuilder);
			level2Holder.description.setVisibility(View.VISIBLE);
		} else {
			level2Holder.platform.setText(String.format("%s has no trains approaching", currentLevel2.getName()));
			level2Holder.description.setVisibility(View.GONE);
		}
	}

	@Override
	protected int getLevel3LayoutId() {
		return R.layout.item_prediction_summary_train;
	}
	@Override
	protected TrainViewHolder createLevel3Holder(View level3ConvertView) {
		return null;
	}
	@Override
	protected void bindLevel3View(TrainViewHolder level3Holder, Station currentLevel1, Platform currentLevel2,
			Train currentLevel3, View level3ConvertView) {}

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
		if (m_directions.isEmpty()) {
			return groups; // shortcut to show everything when there's nothing filtered
		}
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
		if (m_directions.isEmpty()) {
			return children; // shortcut to show everything when there's nothing filtered
		}
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
