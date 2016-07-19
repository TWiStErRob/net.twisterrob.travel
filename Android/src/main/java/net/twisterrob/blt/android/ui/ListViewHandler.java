package net.twisterrob.blt.android.ui;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import net.twisterrob.android.utils.tools.AndroidTools;

public class ListViewHandler {

	private TextView m_emptyText;
	private AbsListView m_listView;

	public ListViewHandler(Activity activity, AbsListView listView, int emptyViewID,
			final SwipeRefreshLayout.OnRefreshListener refreshListener) {
		m_listView = listView;

		m_emptyText = (TextView)activity.findViewById(emptyViewID);
		m_listView.setEmptyView(m_emptyText);
		AndroidTools.visibleIfHasText(m_emptyText);

		if (refreshListener != null) {
			m_emptyText.setOnClickListener(new OnClickListener() {
				@Override public void onClick(View v) {
					refreshListener.onRefresh();
				}
			});
		}
	}

	public void update(String message, ListAdapter adapter) {
		setMessage(message);
		//noinspection RedundantCast AbsListView.setAdapter(ListAdapter) was added in API 11
		((ListView)m_listView).setAdapter(adapter);
	}
	public void update(String message, ExpandableListAdapter adapter) {
		setMessage(message);
		((ExpandableListView)m_listView).setAdapter(adapter);
	}
	private void setMessage(String message) {
		m_emptyText.setText(message);
		AndroidTools.visibleIfHasText(m_emptyText);
	}

	public void empty(String message) {
		if (m_listView instanceof ExpandableListView) {
			update(message, (ExpandableListAdapter)null);
		} else if (m_listView instanceof ListView) {
			update(message, (ListAdapter)null);
		} else {
			throw new IllegalStateException(m_listView.getClass() + " is not handled");
		}
	}
	public void startTFLLoad() {
		String message = "Please wait while data is being retrieved from TFL..";
		empty(message);
	}
}
