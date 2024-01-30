package io.foundy.feature.search.data.repository

import io.foundy.core.model.SearchedUser

interface SearchRepository {
    suspend fun searchUsers(userName: String): Result<List<SearchedUser>>
}
