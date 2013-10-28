package net.twisterrob.blt.android.ui.activity.main;

import android.app.Activity;
import android.content.*;

class LauncherItem {
	private int m_titleResource;
	private Class<? extends Activity> m_targetActivityClass;

	public LauncherItem(int titleResource, Class<? extends Activity> targetActivityClass) {
		m_titleResource = titleResource;
		m_targetActivityClass = targetActivityClass;
	}

	public int getTitle() {
		return m_titleResource;
	}
	public String getTitle(Context context) {
		return context.getString(m_titleResource);
	}

	Intent createIntent(Context context) {
		Intent intent = new Intent(context, m_targetActivityClass);
		addIntentParams(intent);
		return intent;
	}
	/**
	 * @param intent to add params to
	 */
	void addIntentParams(Intent intent) {
		// optional @Override
	}
}