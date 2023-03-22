package io.foundy.welcome.data.repository

import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.Tag
import io.foundy.welcome.data.api.WelcomeApi
import io.foundy.welcome.data.model.toEntity
import javax.inject.Inject

class NetworkWelcomeRepository @Inject constructor(
    private val api: WelcomeApi
) : WelcomeRepository {

    override suspend fun getTags(name: String): Result<List<Tag>> {
        return runCatching {
            val response = api.getTags(page = 0, name = name)
            response.getDataOrThrowMessage().map { it.toEntity() }
        }
    }
}
