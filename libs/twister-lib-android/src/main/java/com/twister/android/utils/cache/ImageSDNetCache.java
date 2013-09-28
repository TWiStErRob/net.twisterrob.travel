package com.twister.android.utils.cache;

import java.io.IOException;
import java.net.URL;

import android.graphics.Bitmap;

import com.twister.android.utils.LibContextProvider;
import com.twister.android.utils.cache.lowlevel.*;
import com.twister.android.utils.cache.lowlevel.ImageCache.ImageCacheParams;
import com.twister.android.utils.log.*;
import com.twister.android.utils.tools.*;

public class ImageSDNetCache implements Cache<URL, Bitmap> {
	private static final Log LOG = LogFactory.getLog(Tag.IO);
	private final ImageCache m_cache;

	public ImageSDNetCache() {
		ImageCacheParams params = new ImageCache.ImageCacheParams(LibContextProvider.getApplicationContext(), "");
		params.initDiskCacheOnCreate = true;
		params.memoryCacheEnabled = false;
		m_cache = new ImageCache(params);
	}

	public Bitmap get(final URL key) throws IOException {
		if (key == null) {
			return null;
		}
		Bitmap bitmap = null;
		bitmap = m_cache.getBitmapFromDiskCache(key.toString());
		if (bitmap == null) {
			bitmap = getImage(key);
			put(key, bitmap);
		}
		return bitmap;
	}

	private Bitmap getImage(final URL key) throws IOException {
		try {
			Bitmap bitmap = IOTools.getImage(key);
			return bitmap;
		} catch (IOException ex) {
			IOException newEx = new IOException(StringTools.format("Cannot get image: %s", key), ex);
			LOG.warn(newEx.getMessage(), ex);
			throw newEx;
		}
	}

	public void put(final URL url, final Bitmap image) {
		if (url != null && image != null) {
			m_cache.addBitmapToCache(url.toString(), image);
		}
	}
}
