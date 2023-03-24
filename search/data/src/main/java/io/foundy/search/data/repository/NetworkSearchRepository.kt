package io.foundy.search.data.repository

import io.foundy.auth.data.repository.AuthRepository
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.SearchedUser
import io.foundy.search.data.api.SearchApi
import io.foundy.search.data.model.toEntity
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class NetworkSearchRepository @Inject constructor(
    private val api: SearchApi,
    private val authRepository: AuthRepository
) : SearchRepository {

    override suspend fun searchUsers(userName: String): Result<List<SearchedUser>> {
        val currentUserId = authRepository.currentUserIdStream.firstOrNull()
        check(currentUserId != null)
        return runCatching {
            val response = api.searchUsers(name = userName, exceptUserId = currentUserId)
            response.getDataOrThrowMessage().map { it.toEntity() }
        }
    }
}
