package net.twisterrob.blt.android;

public class BuildConfigWrapper implements BuildConfig {

	@Override public boolean isDebug() {
		return net.twisterrob.blt.android.app.full.BuildConfig.DEBUG;
	}
}
