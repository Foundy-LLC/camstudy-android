package io.foundy.auth.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    // TODO: :domain 모듈을 만들고 GetCurrentUserIdUseCase 클래스 생성하기
    val currentUserIdStream: Flow<String?>

    /**
     * `currentUserIdStream`이 `true`인데 이 값이 `null`인 경우 서버와의 연결을 실패한 경우이다.
     */
    val existsInitInfo: Boolean?

    suspend fun markAsUserInitialInfoExists(userId: String)
}
