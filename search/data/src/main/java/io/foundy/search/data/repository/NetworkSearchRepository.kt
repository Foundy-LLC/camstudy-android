package io.foundy.search.data.repository

import io.foundy.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.SearchedUser
import io.foundy.search.data.api.SearchApi
import io.foundy.search.data.model.toEntity
import javax.inject.Inject

class NetworkSearchRepository @Inject constructor(
    private val api: SearchApi,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase
) : SearchRepository {

    override suspend fun searchUsers(userName: String): Result<List<SearchedUser>> {
        val currentUserId = getCurrentUserIdUseCase()
        check(currentUserId != null)
        return runCatching {
            val response = api.searchUsers(name = userName, exceptUserId = currentUserId)
            response.getDataOrThrowMessage().users.map { it.toEntity() }
        }
    }
}
