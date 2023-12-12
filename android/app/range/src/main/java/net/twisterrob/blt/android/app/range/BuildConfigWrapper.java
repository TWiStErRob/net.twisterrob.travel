package net.twisterrob.blt.android.app.range;

class BuildConfigWrapper implements net.twisterrob.blt.android.BuildConfig {

	@Override public boolean isDebug() {
		return BuildConfig.DEBUG;
	}

	@Override public String applicationId() {
		return BuildConfig.APPLICATION_ID;
	}
}
