package net.twisterrob.blt.gapp.view.handlebars;

import com.github.jknack.handlebars.Handlebars;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.handlebars.HandlebarsViewsRenderer;
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfiguration;
import jakarta.inject.Singleton;

@Singleton
@Replaces(HandlebarsViewsRenderer.class)
public final class CustomHandlebarsViewsRenderer<T, R> extends HandlebarsViewsRenderer<T, R> {

	public CustomHandlebarsViewsRenderer(
			ViewsConfiguration viewsConfiguration,
			ClassPathResourceLoader resourceLoader,
			HandlebarsViewsRendererConfiguration handlebarsViewsRendererConfiguration,
			Handlebars handlebars
	) {
		super(viewsConfiguration, resourceLoader, handlebarsViewsRendererConfiguration, handlebars);

		this.handlebars.registerHelpers(HandlebarsHelpers.class);
	}
}
