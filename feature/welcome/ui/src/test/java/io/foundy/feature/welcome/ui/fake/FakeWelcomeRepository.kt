package io.foundy.feature.welcome.ui.fake

import io.foundy.core.model.Tag
import io.foundy.feature.welcome.data.repository.WelcomeRepository
import javax.inject.Inject

class FakeWelcomeRepository @Inject constructor() : WelcomeRepository {

    override suspend fun getTags(name: String): Result<List<Tag>> {
        return Result.success(emptyList())
    }
}
