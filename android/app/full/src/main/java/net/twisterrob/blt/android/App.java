package net.twisterrob.blt.android;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import androidx.annotation.NonNull;

import net.twisterrob.android.AndroidConstants;
import net.twisterrob.android.app.BaseApp;
import net.twisterrob.android.log.AndroidLoggerFactory;
import net.twisterrob.android.utils.concurrent.BackgroundExecution;
import net.twisterrob.android.utils.tostring.stringers.detailed.RangeStringers;
import net.twisterrob.blt.android.app.full.BuildConfig;
import net.twisterrob.blt.android.data.AndroidDBStaticData;
import net.twisterrob.blt.android.data.AndroidStaticData;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.blt.io.feeds.LocalhostUrlBuilder;
import net.twisterrob.blt.io.feeds.TFLUrlBuilder;
import net.twisterrob.blt.io.feeds.URLBuilder;
import net.twisterrob.blt.io.feeds.trackernet.TrackerNetData;
import net.twisterrob.java.utils.tostring.StringerRepo;

public class App extends BaseApp implements Injector.Provider {
	static {
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.blt\\.android\\.(.+\\.)?", "");
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.blt\\.(.+\\.)?", "");
	}

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	private static final boolean ALLOW_MOCK_URLS = false;

	private AndroidStaticData m_static;
	private URLBuilder m_urlBuilder = useMockUrls()
			? new LocalhostUrlBuilder(new TrackerNetData())
			: new TFLUrlBuilder("papp.robert.s@gmail.com", new TrackerNetData())
			;

	public App() {
		super(BuildConfig.DEBUG, AndroidConstants.INVALID_RESOURCE_ID);
	}

	public static @NonNull App getInstance() {
		return (App)BaseApp.getInstance();
	}

	public static @NonNull DataBaseHelper db() {
		return getInstance().getDatabase();
	}

	@Override public void onCreate() {
		super.onCreate();
		RangeStringers.register(StringerRepo.INSTANCE);
		m_static = new AndroidDBStaticData(db());
	}

	@Override protected Object createDatabase() {
		DataBaseHelper db = new DataBaseHelper(this, BuildConfig.DEBUG);
		@SuppressWarnings({"unused", "deprecation"}) // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		Object task = new BackgroundExecution(new Runnable() {
			@Override public void run() {
				db().openDB();
			}
		}).execute();
		return db;
	}

	protected static boolean useMockUrls() {
		return BuildConfig.DEBUG && ALLOW_MOCK_URLS;
	}

	public URLBuilder getUrls() {
		return m_urlBuilder;
	}

	public AndroidStaticData getStaticData() {
		return m_static;
	}

	@SuppressWarnings("deprecation")
	@Override public Injector injector() {
		return new net.twisterrob.blt.android.ui.activity.Injector(
				new BuildConfigWrapper(),
				m_static,
				db(),
				prefs()
		);
	}
}
