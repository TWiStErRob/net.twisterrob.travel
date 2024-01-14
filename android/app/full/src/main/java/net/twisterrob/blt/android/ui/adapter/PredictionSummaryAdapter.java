package net.twisterrob.blt.android.ui.adapter;

import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.*;

import net.twisterrob.android.adapter.BaseFilteringExpandableList3Adapter;
import net.twisterrob.blt.android.App;
import net.twisterrob.blt.android.app.full.R;
import net.twisterrob.blt.android.ui.adapter.PredictionSummaryAdapter.*;
import net.twisterrob.blt.io.feeds.trackernet.model.*;
import net.twisterrob.blt.model.*;

public class PredictionSummaryAdapter
		extends
		BaseFilteringExpandableList3Adapter<Station, Platform, Train, GroupViewHolder, ChildViewHolder, TrainViewHolder> {
	private static final LineColorer colors = new LineColorer(App.getInstance().getStaticData().getLineColors());

	public PredictionSummaryAdapter(final Context context, ExpandableListView outerList,
			Map<Station, Map<Platform, List<Train>>> data, Collection<PlatformDirection> directionsEnabled) {
		super(context, outerList, data);
		m_directions.addAll(directionsEnabled);
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
		TextView time;
	}

	@Override protected Map<Station, List<Platform>> createChildrenMap() {
		return new TreeMap<>(new Comparator<Station>() {
			public int compare(Station lhs, Station rhs) {
				int first = Station.COMPARATOR_NAME.compare(lhs, rhs);
				return first != 0? first : lhs.getLine().compareTo(rhs.getLine());
			}
		});
	}

	@Override protected int getLevel1LayoutId() {
		return R.layout.item_prediction_summary_station;
	}
	@Override protected GroupViewHolder createGroupHolder(View groupConvertView) {
		GroupViewHolder groupHolder = new GroupViewHolder();
		groupHolder.title = (TextView)groupConvertView.findViewById(R.id.prediction_station_name);
		return groupHolder;
	}

	@Override protected void bindLevel1View(GroupViewHolder level1Holder,
			Station currentLevel1, List<Platform> currentLevel2, View level1ConvertView) {
		String title = String.format("[%s] %s", currentLevel1.getTrackerNetCode(), currentLevel1.getName());
		level1Holder.title.setText(title);
		level1Holder.title.setBackgroundColor(colors.getBackground(currentLevel1.getLine()));
		level1Holder.title.setTextColor(colors.getForeground(currentLevel1.getLine()));
	}

	@Override protected int getLevel2LayoutId() {
		return R.layout.item_prediction_summary_platform;
	}
	@Override protected ChildViewHolder createLevel2Holder(View level2ConvertView) {
		ChildViewHolder childHolder = new ChildViewHolder();
		childHolder.platform = (TextView)level2ConvertView.findViewById(R.id.prediction_platform_name);
		childHolder.platform.setBackgroundColor(Color.rgb(224, 255, 255));
		childHolder.description = (TextView)level2ConvertView.findViewById(R.id.prediction_platform_description);
		return childHolder;
	}
	@Override protected void bindLevel2View(ChildViewHolder level2Holder,
			Station currentLevel1, Platform currentLevel2, List<Train> currentLevel3, View level2ConvertView) {
		String platformDesc;
		if (currentLevel3.size() > 0) {
			platformDesc = m_context.getResources().getQuantityString(R.plurals.prediction_approach,
					currentLevel3.size(), currentLevel2.getName(), currentLevel3.size());
		} else {
			platformDesc = m_context.getString(R.string.prediction_approach_zero,
					currentLevel2.getName());
		}
		level2Holder.platform.setText(platformDesc);
		level2Holder.description.setVisibility(View.GONE);
	}

	@Override protected void bindLevel2Groups(ExpandableListView list, BaseExpandableListAdapter adapter) {
		int groups = adapter.getGroupCount();
		for (int i = 0; i < groups; ++i) {
			list.expandGroup(i);
		}
	}

	@Override protected int getLevel3LayoutId() {
		return R.layout.item_prediction_summary_train;
	}
	@Override protected TrainViewHolder createLevel3Holder(View level3ConvertView) {
		TrainViewHolder trainHolder = new TrainViewHolder();
		trainHolder.train = (TextView)level3ConvertView.findViewById(R.id.prediction_train_name);
		trainHolder.description = (TextView)level3ConvertView.findViewById(R.id.prediction_train_description);
		trainHolder.time = (TextView)level3ConvertView.findViewById(R.id.prediction_train_time);
		return trainHolder;
	}
	@Override protected void bindLevel3View(TrainViewHolder level3Holder,
			Station currentLevel1, Platform currentLevel2, Train currentLevel3, View level3ConvertView) {
		String timeString = String.format("%1$tM:%1$tS", currentLevel3.getTimeToStation());
		level3Holder.train.setText(currentLevel3.getDestinationName());
		level3Holder.description.setText(currentLevel3.getLocation());
		level3Holder.time.setText(timeString);
	}

	private Set<PlatformDirection> m_directions = EnumSet.noneOf(PlatformDirection.class);

	public void addDirection(PlatformDirection dir) {
		m_directions.add(dir);
	}
	public void removeDirection(PlatformDirection dir) {
		m_directions.remove(dir);
	}

	@Override protected List<Station> filterGroups(List<Station> groups) {
		if (m_directions.isEmpty()) {
			return groups; // shortcut to show everything when there's nothing filtered
		}
		List<Station> filtered = new ArrayList<>();
		for (Station station : groups) {
			if (matches(getChildren(station))) {
				filtered.add(station);
			}
		}
		return filtered;
	}

	@Override protected List<Platform> filterChildren(List<Platform> children, Station group) {
		if (m_directions.isEmpty()) {
			return children; // shortcut to show everything when there's nothing filtered
		}
		List<Platform> filtered = new ArrayList<>();
		for (Platform platform : children) {
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
		for (Platform platform : currentChildren) {
			matches = matches || matches(platform);
		}
		return matches;
	}
}
