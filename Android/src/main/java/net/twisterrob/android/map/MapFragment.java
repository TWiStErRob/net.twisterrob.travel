package net.twisterrob.android.map;

import android.content.Context;
import android.os.*;
import android.util.AttributeSet;

import com.google.android.gms.maps.SupportMapFragment;

public class MapFragment extends SupportMapFragment {
	@Override public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
		StrictMode.ThreadPolicy originalPolicy = StrictMode.allowThreadDiskReads();
		try {
			super.onInflate(context, attrs, savedInstanceState);
		} finally {
			StrictMode.setThreadPolicy(originalPolicy);
		}
	}
}
