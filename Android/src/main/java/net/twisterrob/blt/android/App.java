package net.twisterrob.blt.android;

import org.slf4j.*;

import androidx.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.*;

import net.twisterrob.android.AndroidConstants;
import net.twisterrob.android.app.BaseApp;
import net.twisterrob.android.log.AndroidLoggerFactory;
import net.twisterrob.android.utils.concurrent.*;
import net.twisterrob.android.utils.tostring.stringers.detailed.*;
import net.twisterrob.blt.android.data.*;
import net.twisterrob.blt.android.db.DataBaseHelper;
import net.twisterrob.blt.io.feeds.*;
import net.twisterrob.java.utils.tostring.StringerRepo;

public class App extends BaseApp {
	static {
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.blt\\.android\\.(.+\\.)?", "");
		AndroidLoggerFactory.addReplacement("^net\\.twisterrob\\.blt\\.(.+\\.)?", "");
	}

	private static final Logger LOG = LoggerFactory.getLogger(App.class);

	private static final boolean ALLOW_MOCK_URLS = false;

	private AndroidStaticData m_static;
	private URLBuilder m_urlBuilder = useMockUrls()? new LocalhostUrlBuilder() : new TFLUrlBuilder(
			"papp.robert.s@gmail.com");

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
		StringerRepo.INSTANCE.register(Status.class, new StatusStringer());
		StringerRepo.INSTANCE.register(Place.class, new PlaceStringer());
		StringerRepo.INSTANCE.register(LatLng.class, new LatLngStringer());
		StringerRepo.INSTANCE.register(LatLngBounds.class, new LatLngBoundsStringer());
		m_static = new AndroidDBStaticData(db());
	}

	@Override protected Object createDatabase() {
		DataBaseHelper db = new DataBaseHelper(this);
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

	public static void sendMail(String body) {
		@SuppressWarnings({"unused", "deprecation"}) // TODO https://github.com/TWiStErRob/net.twisterrob.travel/issues/15
		Object task = new MailSenderAsyncTask("Better London Travel",
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
