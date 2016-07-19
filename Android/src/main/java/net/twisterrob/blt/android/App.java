package net.twisterrob.blt.android;

import org.slf4j.*;
import org.slf4j.impl.AndroidLoggerFactory;

import android.widget.Toast;

import net.twisterrob.android.utils.LibContextProvider;
import net.twisterrob.android.utils.concurrent.MailSenderAsyncTask;
import net.twisterrob.blt.android.data.*;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.blt.io.feeds.*;

public class App extends android.app.Application {
	static {
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.blt\\.android\\.(.+\\.)?", "");
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.blt\\.(.+\\.)?", "");
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.android\\.(.+\\.)?", "");
	}

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	private static /*final*/ App s_instance;
	private static final boolean ALLOW_MOCK_URLS = false;

	private AndroidStaticData m_static;

	public App() {
		LOG.trace("App()");
		s_instance = this;
	}

	public static App getInstance() {
		return s_instance;
	}

	@Override public void onCreate() {
		LOG.trace("App.onCreate()");
		super.onCreate();
		LibContextProvider.setApplicationContext(this);
		DataBaseHelper db = getDataBaseHelper();
		db.openDB();
		m_static = new AndroidDBStaticData(db);
	}

	private volatile DataBaseHelper m_dataBaseHelper = null;
	private URLBuilder m_urlBuilder = useMockUrls()? new LocalhostUrlBuilder() : new TFLUrlBuilder(
			"papp.robert.s@gmail.com");

	protected static boolean useMockUrls() {
		return BuildConfig.DEBUG && ALLOW_MOCK_URLS;
	}

	public static void exit() {
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public URLBuilder getUrls() {
		return m_urlBuilder;
	}

	public AndroidStaticData getStaticData() {
		return m_static;
	}

	public DataBaseHelper getDataBaseHelper() {
		DataBaseHelper helper = m_dataBaseHelper;
		if (helper == null) {
			// TODO double-checked locking doesn't work, use pre-created instance
			synchronized (this) {
				if (helper == null) {
					helper = m_dataBaseHelper = new DataBaseHelper(this);
				}
			}
		}
		return helper;
	}

	public static void sendMail(String body) {
		new MailSenderAsyncTask("Better London Travel",
				"better-london-travel@twisterrob.net", "papp.robert.s@gmail.com") {
			@Override protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (Boolean.TRUE.equals(result)) {
					Toast.makeText(getInstance(), "Mail sent", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getInstance(), "Mail failed", Toast.LENGTH_SHORT).show();
				}
			}
		}.execute(body);
	}
}
