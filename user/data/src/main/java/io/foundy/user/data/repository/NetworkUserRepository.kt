package io.foundy.user.data.repository

import io.foundy.auth.domain.usecase.GetCurrentUserIdUseCase
import io.foundy.auth.domain.usecase.MarkAsUserInitialInfoExistsUseCase
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.User
import io.foundy.crop.data.api.CropApi
import io.foundy.crop.data.model.toEntity
import io.foundy.ranking.data.api.RankingApi
import io.foundy.user.data.model.UserCreateRequestBody
import io.foundy.user.data.model.UserUpdateRequestBody
import io.foundy.user.data.model.toEntity
import io.foundy.user.data.source.UserRemoteDataSource
import io.foundy.user.domain.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(
    private val userDataSource: UserRemoteDataSource,
    private val getCurrentUserIdUseCase: GetCurrentUserIdUseCase,
    private val markAsUserInitialInfoExistsUseCase: MarkAsUserInitialInfoExistsUseCase,
    private val rankingApi: RankingApi,
    // TODO: CropDataSource 구현하기면 수정하기
    private val cropApi: CropApi,
) : UserRepository {

    override suspend fun getUser(id: String): Result<User> {
        return runCatching {
            val currentUserId = requireNotNull(getCurrentUserIdUseCase()) {
                "로그인 없이 다른 회원 정보를 열람했습니다."
            }
            return@runCatching coroutineScope {
                val userDeferred = async {
                    userDataSource.getUser(
                        userId = id,
                        requesterId = currentUserId
                    ).getDataOrThrowMessage()
                }
                val weeklyRankingDeferred = async {
                    rankingApi.getUserRanking(isWeekly = true, userId = id).getDataOrThrowMessage()
                }
                val totalRankingDeferred = async {
                    rankingApi.getUserRanking(isWeekly = false, userId = id).getDataOrThrowMessage()
                }
                val harvestedCropsDeferred = async {
                    cropApi.getHarvestedCrops(userId = id).getDataOrThrowMessage()
                }
                val growingCropDeferred = async {
                    val response = cropApi.getGrowingCrop(userId = id)
                    if (response.code() == 404) {
                        return@async null
                    }
                    return@async response.getDataOrThrowMessage()
                }

                val weeklyRanking = weeklyRankingDeferred.await()
                val totalRanking = totalRankingDeferred.await()
                val harvestedCrops = harvestedCropsDeferred.await()
                val growingCrop = growingCropDeferred.await()

                return@coroutineScope userDeferred.await()
                    .toEntity(
                        isMe = currentUserId == id,
                        weeklyRanking = weeklyRanking.user.ranking,
                        totalRanking = totalRanking.user.ranking,
                        weeklyStudyTimeSec = weeklyRanking.user.studyTimeSec,
                        weeklyRankingOverall = 100 * weeklyRanking.user.ranking /
                            weeklyRanking.totalUserCount,
                        harvestedCrops = harvestedCrops.map { it.toEntity() },
                        growingCrop = growingCrop?.toEntity()
                    )
            }
        }
    }

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
            userDataSource.postUserInitialInfo(body = requestBody).getDataOrThrowMessage()
            markAsUserInitialInfoExistsUseCase(userId = userId)
            if (profileImage != null) {
                val multipart = MultipartBody.Part.createFormData(
                    PROFILE_IMAGE_KEY,
                    profileImage.name,
                    profileImage.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                userDataSource.uploadUserProfileImage(userId, multipart).getDataOrThrowMessage()
            }
        }
    }

    override suspend fun updateUserProfile(
        name: String,
        introduce: String?,
        tags: List<String>,
        profileImage: File?,
        shouldRemoveProfileImage: Boolean
    ): Result<String?> {
        if (profileImage != null) {
            require(!shouldRemoveProfileImage) { "업로드할 프로필 이지미가 있는 경우 기본 이미지로 설정할 수 없습니다" }
        }
        if (shouldRemoveProfileImage) {
            require(profileImage == null) { "기본 이미지로 바꿀때는 profileImage가 null이어야 합니다" }
        }
        val currentUserId = requireNotNull(getCurrentUserIdUseCase()) {
            "로그인 없이 다른 회원 정보를 업데이트하려 했습니다."
        }
        val requestBody = UserUpdateRequestBody(
            userId = currentUserId,
            nickName = name,
            introduce = introduce,
            tags = tags,
        )
        return runCatching {
            var newImageUrl: String? = null
            // TODO: async await로 성능 향상하기
            userDataSource.updateUserProfile(userId = currentUserId, body = requestBody)
                .getDataOrThrowMessage()
            if (profileImage != null) {
                val multipart = MultipartBody.Part.createFormData(
                    PROFILE_IMAGE_KEY,
                    profileImage.name,
                    profileImage.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                newImageUrl = userDataSource.uploadUserProfileImage(currentUserId, multipart)
                    .getDataOrThrowMessage()
            } else if (shouldRemoveProfileImage) {
                userDataSource.removeProfileImage(userId = currentUserId)
                    .getDataOrThrowMessage()
            }
            return@runCatching newImageUrl
        }
    }

    companion object {
        const val PROFILE_IMAGE_KEY = "profileImage"
    }
}
