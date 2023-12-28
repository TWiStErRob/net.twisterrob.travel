package net.twisterrob.travel.statushistory

import io.micronaut.runtime.Micronaut

object Application {

	@JvmStatic
	fun main(vararg args: String) {
		Micronaut
			.build(*args)
			.classes(Application::class.java)
			.banner(false)
			.start()
			.use { _ ->
				// Nothing yet, wrap in try-with-resources to ensure teardown.
			}
	}
}
