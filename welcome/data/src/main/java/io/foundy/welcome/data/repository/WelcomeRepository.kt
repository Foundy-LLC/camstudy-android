package io.foundy.welcome.data.repository

import io.foundy.core.model.Tag

interface WelcomeRepository {
    suspend fun getTags(name: String): Result<List<Tag>>
}
