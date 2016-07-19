package net.twisterrob.blt.android.ui.adapter;

import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import net.twisterrob.android.adapter.BaseExpandableList2Adapter;
import net.twisterrob.blt.android.R;
import net.twisterrob.blt.android.ui.adapter.PredictionDetailsAdapter.*;
import net.twisterrob.blt.io.feeds.trackernet.model.*;

public class PredictionDetailsAdapter
		extends
		BaseExpandableList2Adapter<Platform, Train, PlatformViewHolder, TrainViewHolder> {

	public PredictionDetailsAdapter(Context context, Collection<Platform> groups,
			Map<Platform, ? extends List<Train>> children) {
		super(context, groups, children);
	}

	protected static class PlatformViewHolder {
		TextView platform;
		TextView description;
	}

	protected static class TrainViewHolder {
		TextView train;
		TextView description;
		TextView time;
	}

	@Override protected int getGroupLayoutId() {
		return R.layout.item_prediction_summary_platform;
	}
	@Override protected PlatformViewHolder createGroupHolder(View groupConvertView) {
		PlatformViewHolder childHolder = new PlatformViewHolder();
		childHolder.platform = (TextView)groupConvertView.findViewById(R.id.prediction_platform_name);
		childHolder.platform.setBackgroundColor(Color.rgb(224, 255, 255));
		childHolder.description = (TextView)groupConvertView.findViewById(R.id.prediction_platform_description);
		return childHolder;
	}
	@Override protected void bindGroupView(PlatformViewHolder groupHolder, Platform currentGroup,
			List<Train> currentChildren,
			View groupConvertView) {
		String platformDesc;
		if (currentChildren.size() > 0) {
			platformDesc = m_context.getResources().getQuantityString(R.plurals.prediction_approach,
					currentChildren.size(), currentGroup.getName(), currentChildren.size());
		} else {
			platformDesc = m_context.getString(R.string.prediction_approach_zero,
					currentGroup.getName());
		}
		groupHolder.platform.setText(platformDesc);
		groupHolder.description.setVisibility(View.GONE);
	}
	@Override protected int getChildLayoutId() {
		return R.layout.item_prediction_summary_train;
	}
	@Override protected TrainViewHolder createChildHolder(View childConvertView) {
		TrainViewHolder trainHolder = new TrainViewHolder();
		trainHolder.train = (TextView)childConvertView.findViewById(R.id.prediction_train_name);
		trainHolder.description = (TextView)childConvertView.findViewById(R.id.prediction_train_description);
		trainHolder.time = (TextView)childConvertView.findViewById(R.id.prediction_train_time);
		return trainHolder;
	}
	@Override protected void bindChildView(TrainViewHolder childHolder, Platform currentGroup, Train currentChild,
			View childConvertView) {
		String timeString = String.format(Locale.getDefault(), "%1$tM:%1$tS", currentChild.getTimeToStation());
		childHolder.train.setText(currentChild.getDestinationName());
		childHolder.description.setText(currentChild.getLocation());
		childHolder.time.setText(timeString);
	}
}
