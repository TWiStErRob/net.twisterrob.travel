package net.twisterrob.travel.statushistory.infrastructure.github

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Requires

@Bean
@Requires(notEnv = ["production"])
class ThrowErrorFeedbackInteractor : SendFeedbackUseCase {
	override fun report(title: String, body: String) {
		error("${title}\n${body}")
	}
}
