package net.twisterrob.blt.android;

import java.net.URL;

import net.twisterrob.android.app.AppCaches;
import net.twisterrob.android.mail.MailSenderAsyncTask;
import net.twisterrob.android.utils.LibContextProvider;
import net.twisterrob.android.utils.cache.Cache;
import net.twisterrob.blt.android.data.*;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.blt.io.feeds.*;

import org.slf4j.*;

import android.graphics.Bitmap;
import android.widget.Toast;

public class App extends android.app.Application {
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	private static/* final */App s_instance;
	private static final boolean DEBUG = false;

	private AndroidStaticData m_static;
	private AppCaches m_caches;

	public App() {
		LOG.trace("App()");
		s_instance = this;
	}

	public static App getInstance() {
		return s_instance;
	}

	@Override
	public void onCreate() {
		LOG.trace("App.onCreate()");
		super.onCreate();
		LibContextProvider.setApplicationContext(this);
		DataBaseHelper db = getDataBaseHelper();
		db.openDB();
		m_static = new AndroidDBStaticData(db);
	}

	private volatile DataBaseHelper m_dataBaseHelper = null;
	private URLBuilder m_urlBuilder = isDebug()? new LocalhostUrlBuilder() : new TFLUrlBuilder(
			"papp.robert.s@gmail.com");

	protected static boolean isDebug() {
		return BuildConfig.DEBUG && DEBUG;
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
			synchronized (this) {
				if (helper == null) {
					helper = m_dataBaseHelper = new DataBaseHelper(this);
				}
			}
		}
		return helper;
	}

	public Cache<URL, Bitmap> getPosterCache() {
		return m_caches.getCache(AppCaches.CACHE_IMAGE);
	}

	public static void sendMail(String body) {
		MailSenderAsyncTask.setUsername("*********@********.****");
		MailSenderAsyncTask.setPassword("*********");
		MailSenderAsyncTask task = new MailSenderAsyncTask("Better London Travel",
				"better-london-travel@twisterrob.net", "papp.robert.s@gmail.com") {
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (Boolean.TRUE.equals(result)) {
					Toast.makeText(getInstance(), "Mail sent", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getInstance(), "Mail failed", Toast.LENGTH_SHORT).show();
				}
			}
		};
		task.setBody(body);
		task.execute();
	}
}
