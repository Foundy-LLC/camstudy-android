package io.foundy.feature.search.data.model

data class SearchUsersResponse(
    val maxPage: Int,
    val users: List<SearchedUserDto>
)
