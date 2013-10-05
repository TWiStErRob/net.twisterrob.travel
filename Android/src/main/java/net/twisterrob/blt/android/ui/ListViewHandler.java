package net.twisterrob.blt.android.ui;

import android.app.Activity;
import android.widget.*;

public class ListViewHandler {

	private TextView m_emptyText;
	private AbsListView m_listView;

	public ListViewHandler(Activity activity, AbsListView listView, int emptyViewID) {
		m_listView = listView;

		m_emptyText = (TextView)activity.findViewById(emptyViewID);
		m_listView.setEmptyView(m_emptyText);
	}

	public void update(String message, ListAdapter adapter) {
		m_emptyText.setText(message);
		((ListView)m_listView).setAdapter(adapter);
	}
	public void update(String message, ExpandableListAdapter adapter) {
		m_emptyText.setText(message);
		((ExpandableListView)m_listView).setAdapter(adapter);
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
