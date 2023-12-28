package net.twisterrob.travel.statushistory.viewmodel

/**
 * Last `java8` legacy deployment.
 * ```
 * applicationId: com.google.appengine.application.id=twisterrob-london
 * applicationVersion: com.google.appengine.application.version=20221107t101946.447701058168130218
 * environment: com.google.appengine.runtime.environment=Production
 * version: com.google.appengine.runtime.version=Google App Engine/1.9.98
 * ```
 */
class Versions {

	val environmentKey: String
		get() = "NODE_ENV"

	val environment: String?
		get() = System.getenv(environmentKey)

	val versionKey: String
		get() = "GAE_DEPLOYMENT_ID"

	val version: String?
		get() = System.getenv(versionKey)

	val applicationIdKey: String
		get() = "GAE_APPLICATION"

	val applicationId: String?
		get() = System.getenv(applicationIdKey)

	val applicationVersionKey: String
		get() = "GAE_VERSION"

	val applicationVersion: String?
		get() = System.getenv(applicationVersionKey)
}
