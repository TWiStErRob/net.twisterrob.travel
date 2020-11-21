package net.twisterrob.blt.gapp.viewmodel;

import com.google.appengine.api.utils.SystemProperty;

public class Versions {

	public String getEnvironmentKey() {
		return SystemProperty.environment.key();
	}

	public String getEnvironment() {
		return SystemProperty.environment.get();
	}

	public String getVersionKey() {
		return SystemProperty.version.key();
	}

	public String getVersion() {
		return SystemProperty.version.get();
	}

	public String getApplicationIdKey() {
		return SystemProperty.applicationId.key();
	}

	public String getApplicationId() {
		return SystemProperty.applicationId.get();
	}

	public String getApplicationVersionKey() {
		return SystemProperty.applicationVersion.key();
	}

	public String getApplicationVersion() {
		return SystemProperty.applicationVersion.get();
	}
}
