package net.twisterrob.blt.android.ui.activity;

import net.twisterrob.android.content.pref.ResourcePreferences;
import net.twisterrob.blt.android.BuildConfig;
import net.twisterrob.blt.android.data.AndroidStaticData;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.blt.android.ui.activity.RangeMapActivity;
import net.twisterrob.blt.android.ui.activity.RangeNearestFragment;
import net.twisterrob.blt.android.ui.activity.RangeOptionsFragment;

@Deprecated // TODO migrate to Hilt, this is just a temporary solution to minimize changes in rename PR.
public class Injector implements net.twisterrob.blt.android.Injector {

	private final BuildConfig buildConfig;
	private final AndroidStaticData staticData;
	private final DataBaseHelper db;
	private final ResourcePreferences prefs;

	public Injector(
			BuildConfig buildConfig,
			AndroidStaticData staticData,
			DataBaseHelper db,
			ResourcePreferences prefs
	) {
		this.buildConfig = buildConfig;
		this.staticData = staticData;
		this.db = db;
		this.prefs = prefs;
	}

	@Override public void inject(Object target) {
		if (target instanceof RangeMapActivity activity) {
			activity.buildConfig = buildConfig;
			activity.staticData = staticData;
			activity.db = db;
			activity.prefs = prefs;
		} else if (target instanceof RangeOptionsFragment fragment) {
			fragment.prefs = prefs;
			fragment.staticData = staticData;
		} else if (target instanceof RangeNearestFragment fragment) {
			fragment.staticData = staticData;
		} else {
			throw new IllegalArgumentException("Unknown target: " + target);
		}
	}
}
