package net.twisterrob.blt.android;

import net.twisterrob.blt.android.app.full.BuildConfig;

public class BuildConfigWrapper implements net.twisterrob.blt.android.BuildConfig {

	@Override public boolean isDebug() {
		return BuildConfig.DEBUG;
	}

	@Override public String applicationId() {
		return BuildConfig.APPLICATION_ID;
	}
}
