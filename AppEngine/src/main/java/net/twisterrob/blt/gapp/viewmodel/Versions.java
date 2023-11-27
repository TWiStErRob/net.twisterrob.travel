package net.twisterrob.blt.gapp.viewmodel;

/**
 * Last {@code java8} legacy deployment.
 * <pre><code>
 * applicationId: com.google.appengine.application.id=twisterrob-london
 * applicationVersion: com.google.appengine.application.version=20221107t101946.447701058168130218
 * environment: com.google.appengine.runtime.environment=Production
 * version: com.google.appengine.runtime.version=Google App Engine/1.9.98
 * </code></pre>
 */
public class Versions {

	public String getEnvironmentKey() {
		return "NODE_ENV";
	}

	public String getEnvironment() {
		return System.getenv(getEnvironmentKey());
	}

	public String getVersionKey() {
		return "GAE_DEPLOYMENT_ID";
	}

	public String getVersion() {
		return System.getenv(getVersionKey());
	}

	public String getApplicationIdKey() {
		return "GAE_APPLICATION";
	}

	public String getApplicationId() {
		return System.getenv(getApplicationIdKey());
	}

	public String getApplicationVersionKey() {
		return "GAE_VERSION";
	}

	public String getApplicationVersion() {
		return System.getenv(getApplicationVersionKey());
	}
}
