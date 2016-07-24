package net.twisterrob.blt.android;

import org.slf4j.*;
import org.slf4j.impl.AndroidLoggerFactory;

import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.*;

import net.twisterrob.android.utils.concurrent.MailSenderAsyncTask;
import net.twisterrob.android.utils.tools.AndroidTools;
import net.twisterrob.android.utils.tostring.stringers.detailed.*;
import net.twisterrob.blt.android.data.*;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.java.utils.tostring.StringerRepo;

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
		StringerRepo.INSTANCE.register(Status.class, new StatusStringer());
		StringerRepo.INSTANCE.register(Place.class, new PlaceStringer());
		StringerRepo.INSTANCE.register(LatLng.class, new LatLngStringer());
		StringerRepo.INSTANCE.register(LatLngBounds.class, new LatLngBoundsStringer());
		AndroidTools.setContext(this);
//		com.facebook.stetho.Stetho.initializeWithDefaults(this);
		DataBaseHelper db = m_dataBaseHelper = new DataBaseHelper(this);
		db.openDB();
		m_static = new AndroidDBStaticData(db);
	}

	private DataBaseHelper m_dataBaseHelper = null;
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
		return m_dataBaseHelper;
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
