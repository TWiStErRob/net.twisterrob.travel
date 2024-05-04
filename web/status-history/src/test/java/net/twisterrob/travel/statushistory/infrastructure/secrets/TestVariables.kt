package net.twisterrob.travel.statushistory.infrastructure.secrets

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Requires

@Bean(typed = [Variables::class])
@Requires(env = ["test"])
class TestVariables : Variables {

	override val githubActor: String = "invalid"
	override val githubPat: String = "credentials"
}
