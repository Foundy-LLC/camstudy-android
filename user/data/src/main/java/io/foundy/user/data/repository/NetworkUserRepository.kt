package io.foundy.user.data.repository

import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.user.data.model.UserCreateRequestBody
import io.foundy.user.data.source.UserRemoteDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(
    private val userDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun postUserInitialInfo(
        userId: String,
        name: String,
        introduce: String?,
        tags: List<String>,
        profileImage: File?
    ): Result<Unit> {
        val requestBody = UserCreateRequestBody(
            userId = userId,
            name = name,
            introduce = introduce,
            tags = tags,
        )
        return runCatching {
            coroutineScope {
                val deferredUserResponse = async {
                    userDataSource.postUserInitialInfo(body = requestBody)
                }
                if (profileImage != null) {
                    val multipart = MultipartBody.Part.createFormData(
                        PROFILE_IMAGE_KEY,
                        profileImage.name,
                        profileImage.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    )
                    val deferredProfileImageResponse = async {
                        userDataSource.uploadUserProfileImage(userId, multipart)
                    }
                    deferredProfileImageResponse.await().getDataOrThrowMessage()
                }
                deferredUserResponse.await().getDataOrThrowMessage()
            }
        }
    }

    companion object {
        const val PROFILE_IMAGE_KEY = "profileImage"
    }
}
