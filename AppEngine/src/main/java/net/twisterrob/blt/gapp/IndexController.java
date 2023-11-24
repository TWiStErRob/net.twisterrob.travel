package net.twisterrob.blt.gapp;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;

import net.twisterrob.blt.gapp.viewmodel.Versions;

@Controller
public class IndexController {

	@Get("/")
	@View("index")
	public MutableHttpResponse<?> index() {
		return HttpResponse.ok(new IndexModel(new Versions()));
	}

	private record IndexModel(
			Versions versions
	) {

	}
}
