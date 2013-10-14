package net.twisterrob.blt.android;

import java.net.URL;
import java.util.*;

import net.twisterrob.android.mail.MailSenderAsyncTask;
import net.twisterrob.android.utils.LibContextProvider;
import net.twisterrob.android.utils.cache.*;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.blt.io.feeds.*;
import android.graphics.Bitmap;
import android.widget.Toast;

public class App extends android.app.Application {
	private static/* final */App s_instance;
	private static final boolean DEBUG = true;
	private static final String CACHE_IMAGE = ImageSDNetCache.class.getName();
	private static final Map<String, Cache<?, ?>> s_caches = new HashMap<String, Cache<?, ?>>();

	public App() {
		s_instance = this;
	}

	public static App getInstance() {
		return s_instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LibContextProvider.setApplicationContext(this);
		getDataBaseHelper().openDB();
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

	public static Cache<URL, Bitmap> getPosterCache() {
		return getCache(CACHE_IMAGE);
	}

	private static <K, V> Cache<K, V> getCache(final String cacheName) {
		Cache<K, V> cache = (Cache<K, V>)s_caches.get(cacheName);
		if (cache == null) {
			cache = createCache(cacheName);
			s_caches.put(cacheName, cache);
		}
		return cache;
	}

	private static <T> T createCache(final String cacheClass) {
		try {
			return (T)Class.forName(cacheClass).newInstance();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return null;
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
