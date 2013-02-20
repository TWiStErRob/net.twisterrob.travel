package com.twister.london.travel;

import java.net.URL;
import java.util.*;

import android.graphics.Bitmap;

import com.twister.android.utils.cache.*;
import com.twister.london.travel.db.DataBaseHelper;

public class App extends android.app.Application {
	private static/* final */App s_instance;
	private static final String CACHE_IMAGE = ImageSDNetCache.class.getName();
	private static final Map<String, Cache<?, ?>> s_caches = new HashMap<String, Cache<?, ?>>();

	public App() {
		s_instance = this;
	}

	public static App getInstance() {
		return s_instance;
	}

	@Override public void onCreate() {
		super.onCreate();
		getDataBaseHelper().openDB();
	}

	private volatile DataBaseHelper m_dataBaseHelper = null;

	public static void exit() {
		android.os.Process.killProcess(android.os.Process.myPid());
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
		return getCache(CACHE_IMAGE);
	}

	private <K, V> Cache<K, V> getCache(final String cacheName) {
		@SuppressWarnings("unchecked") Cache<K, V> cache = (Cache<K, V>)s_caches.get(cacheName);
		if (cache == null) {
			cache = createCache(cacheName);
			s_caches.put(cacheName, cache);
		}
		return cache;
	}

	@SuppressWarnings("unchecked") private <T> T createCache(final String cacheClass) {
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
}
