package net.twisterrob.travel.statushistory.infrastructure.secrets

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient
import com.google.cloud.secretmanager.v1.SecretVersionName
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Requires
import jakarta.inject.Inject
import jakarta.inject.Provider

/**
 * @param client injected as a provider to delay the creation of the client until it's needed.
 */
@Bean(typed = [Variables::class])
@Requires(notEnv = ["test"])
class GcpSecretVariables @Inject constructor(
	private val client: Provider<SecretManagerServiceClient>,
) : Variables {

	override val githubActor: String
		get() = "TWiStErRob"

	override val githubPat: String
		get() = getSecret("twisterrob-london", "GITHUB_ISSUE_CREATOR")

	private fun getSecret(projectId: String, secretId: String): String {
		val version = SecretVersionName.of(projectId, secretId, "latest")
		val secret = client.get().accessSecretVersion(version)
		return secret.payload.data.toStringUtf8()
	}
}
