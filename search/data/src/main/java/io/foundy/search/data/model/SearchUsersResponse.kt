package io.foundy.search.data.model

data class SearchUsersResponse(
    val maxPage: Int,
    val users: List<SearchedUserDto>
)
