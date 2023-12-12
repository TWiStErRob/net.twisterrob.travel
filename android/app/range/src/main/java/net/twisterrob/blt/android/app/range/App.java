package net.twisterrob.blt.android.app.range;

import android.content.Context;

import androidx.annotation.NonNull;

import net.twisterrob.android.app.BaseApp;
import net.twisterrob.android.log.AndroidLoggerFactory;
import net.twisterrob.android.utils.tostring.stringers.detailed.Stringers;
import net.twisterrob.blt.android.BuildConfig;
import net.twisterrob.blt.android.Injector;
import net.twisterrob.blt.android.data.AndroidDBStaticData;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.java.utils.tostring.StringerRepo;

public class App extends BaseApp implements Injector.Provider {

	static {
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.blt\\.android\\.(.+\\.)?", "");
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.blt\\.(.+\\.)?", "");
	}

	private Injector injector;

	@Override public void onCreate() {
		super.onCreate();
		Stringers.register(StringerRepo.INSTANCE);
		this.injector = createInjector(this);
	}

	@Override public Injector injector() {
		return injector;
	}

	private static @NonNull Injector createInjector(Context context) {
		BuildConfig buildConfig = new BuildConfigWrapper();
		DataBaseHelper db = new DataBaseHelper(context, buildConfig.isDebug());
		@SuppressWarnings("deprecation")
		Injector injector = new net.twisterrob.blt.android.feature.range.Injector(
				buildConfig,
				new AndroidDBStaticData(db),
				db,
				prefs()
		);
		return injector;
	}
}
