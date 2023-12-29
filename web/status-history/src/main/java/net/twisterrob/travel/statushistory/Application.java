package net.twisterrob.travel.statushistory;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;

public class Application {
	@SuppressWarnings("try")
	public static void main(String... args) {
		Micronaut micronaut = Micronaut
				.build(args)
				.classes(Application.class)
				.banner(false)
				;
		try (ApplicationContext context = micronaut.start()) {
			// Nothing yet, wrap in try-with-resources to ensure teardown.
		}
	}
}