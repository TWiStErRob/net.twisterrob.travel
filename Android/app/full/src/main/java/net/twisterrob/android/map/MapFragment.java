package net.twisterrob.android.map;

import android.content.Context;
import android.os.*;
import android.util.AttributeSet;

import com.google.android.gms.maps.SupportMapFragment;

import androidx.annotation.NonNull;

public class MapFragment extends SupportMapFragment {
	@Override public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, Bundle savedInstanceState) {
		StrictMode.ThreadPolicy originalPolicy = StrictMode.allowThreadDiskReads();
		try {
			super.onInflate(context, attrs, savedInstanceState);
		} finally {
			StrictMode.setThreadPolicy(originalPolicy);
		}
	}
}
