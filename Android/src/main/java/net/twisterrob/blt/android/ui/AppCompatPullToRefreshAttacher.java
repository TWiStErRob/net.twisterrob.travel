package net.twisterrob.blt.android.ui;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.*;
import android.app.Activity;

public class AppCompatPullToRefreshAttacher extends PullToRefreshAttacher {
	private Activity m_activity;
	private DefaultHeaderTransformer m_header;
	private final CharSequence m_headerOriginalText;

	public static AppCompatPullToRefreshAttacher get(Activity activity) {
		return get(activity, new Options());
	}

	public static AppCompatPullToRefreshAttacher get(Activity activity, Options options) {
		return new AppCompatPullToRefreshAttacher(activity, options);
	}

	protected AppCompatPullToRefreshAttacher(Activity activity, Options options) {
		super(activity, options);
		m_activity = activity;
		m_headerOriginalText = m_activity
				.getText(uk.co.senab.actionbarpulltorefresh.library.R.string.pull_to_refresh_pull_label);
	}

	public AppCompatPullToRefreshAttacher init(int ptrLayoutId, OnRefreshListener listener) {
		PullToRefreshLayout wrapper = (PullToRefreshLayout)m_activity.findViewById(ptrLayoutId);
		wrapper.setPullToRefreshAttacher(this, listener);
		m_header = (DefaultHeaderTransformer)this.getHeaderTransformer();
		return this;
	}

	public void setLastUpdated(String lastUpdateText) {
		m_header.setPullText(m_headerOriginalText + "\n" + lastUpdateText); // TODO override header
	}

}
