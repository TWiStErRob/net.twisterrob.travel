package net.twisterrob.blt.android.ui.activity.main;

import android.content.*;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.*;

public abstract class MapActivity extends AppCompatActivity {
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	@Override public void onContentChanged() {
		super.onContentChanged();
		initMapIfPlayServicesIsAvailable();
	}
	protected abstract void setupMap();

	private void initMapIfPlayServicesIsAvailable() {
		GoogleApiAvailability api = GoogleApiAvailability.getInstance();
		int result = api.isGooglePlayServicesAvailable(this);
		if (result == ConnectionResult.SUCCESS) {
			setupMap();
		} else if (api.isUserResolvableError(result)) {
			api.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
		} else {
			Toast.makeText(this, api.getErrorString(result), Toast.LENGTH_LONG).show();
		}
	}

	@Override public void startActivityForResult(Intent intent, int requestCode) {
		try {
			super.startActivityForResult(intent, requestCode);
		} catch (NullPointerException ex) {
			// http://stackoverflow.com/a/20905954/253468
			onActivityResult(requestCode, RESULT_CANCELED, null);
		} catch (ActivityNotFoundException ex) {
			// CONSIDER AndroidTools?
			if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {
				Uri data = intent.getData();
				if (data != null && "mailto".equals(data.getScheme())) {
					String message = "You don't have an email app, send an email to: " + data.getSchemeSpecificPart();
					Toast.makeText(this, message, Toast.LENGTH_LONG).show();
					return;
				} else if (data != null && ("http".equals(data.getScheme()) || "https".equals(data.getScheme()))) {
					String message = "You don't have a web browser app, can't display link.";
					Toast.makeText(this, message, Toast.LENGTH_LONG).show();
					return;
				}
			}
			throw ex;
		}
	}
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PLAY_SERVICES_RESOLUTION_REQUEST:
				if (resultCode == RESULT_OK) {
					initMapIfPlayServicesIsAvailable();
				} else {
					Toast.makeText(this, "Google Play Services error resolution failed.", Toast.LENGTH_LONG).show();
				}
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
