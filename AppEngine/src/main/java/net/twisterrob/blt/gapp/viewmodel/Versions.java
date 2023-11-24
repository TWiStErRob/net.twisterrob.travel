package net.twisterrob.blt.gapp.viewmodel;

/**
 * Last old deployment.
 * <pre><code>
 * applicationId: com.google.appengine.application.id=twisterrob-london
 * applicationVersion: com.google.appengine.application.version=20221107t101946.447701058168130218
 * environment: com.google.appengine.runtime.environment=Production
 * version: com.google.appengine.runtime.version=Google App Engine/1.9.98
 * </code></pre>
 * TODO if it doesn't work, try https://cloud.google.com/appengine/docs/standard/java-gen2/runtime#environment_variables
 */
public class Versions {

	public String getEnvironmentKey() {
		return "com.google.appengine.runtime.environment";
	}

	public String getEnvironment() {
		return System.getProperty(getEnvironmentKey());
	}

	public String getVersionKey() {
		return "com.google.appengine.runtime.version";
	}

	public String getVersion() {
		return System.getProperty(getVersionKey());
	}

	public String getApplicationIdKey() {
		return "com.google.appengine.application.id";
	}

	public String getApplicationId() {
		return System.getProperty(getApplicationIdKey());
	}

	public String getApplicationVersionKey() {
		return "com.google.appengine.application.version";
	}

	public String getApplicationVersion() {
		return System.getProperty(getApplicationVersionKey());
	}
}
