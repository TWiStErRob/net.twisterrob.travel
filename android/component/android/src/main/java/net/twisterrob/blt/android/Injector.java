package net.twisterrob.blt.android;

import android.content.Context;

public interface Injector {

	void inject(Object target);

	static Injector from(Context context) {
		return ((Provider)context.getApplicationContext()).injector();
	}

	interface Provider {

		Injector injector();
	}
}
