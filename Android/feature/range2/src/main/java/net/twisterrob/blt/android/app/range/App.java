package net.twisterrob.blt.android.app.range;

import android.app.Application;

import net.twisterrob.android.content.pref.ResourcePreferences;
import net.twisterrob.blt.android.data.AndroidStaticData;

public class App extends Application { // STOPSHIP broken

	public static App getInstance() {
		return null;
	}

	public static ResourcePreferences prefs() {
		return null;
	}

	public static net.twisterrob.blt.android.db.DataBaseHelper db() {
		return null;
	}

	public AndroidStaticData getStaticData() {
		return null;
	}
}
