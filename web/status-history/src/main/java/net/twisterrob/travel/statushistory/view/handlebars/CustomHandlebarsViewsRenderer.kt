package net.twisterrob.travel.statushistory.view.handlebars

import com.github.jknack.handlebars.Handlebars
import io.micronaut.context.annotation.Replaces
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.views.ViewsConfiguration
import io.micronaut.views.handlebars.HandlebarsViewsRenderer
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfiguration
import jakarta.inject.Singleton

@Singleton
@Replaces(HandlebarsViewsRenderer::class)
class CustomHandlebarsViewsRenderer<T, R>(
	viewsConfiguration: ViewsConfiguration,
	resourceLoader: ClassPathResourceLoader,
	handlebarsViewsRendererConfiguration: HandlebarsViewsRendererConfiguration,
	handlebars: Handlebars,
) : HandlebarsViewsRenderer<T, R>(
	viewsConfiguration,
	resourceLoader,
	handlebarsViewsRendererConfiguration,
	handlebars
) {

	init {
		this.handlebars.registerHelpers(HandlebarsHelpers::class.java)
	}
}
