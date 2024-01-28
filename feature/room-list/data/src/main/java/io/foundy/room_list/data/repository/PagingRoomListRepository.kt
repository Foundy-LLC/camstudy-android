package io.foundy.room_list.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.foundy.core.data.extension.getDataOrThrowMessage
import io.foundy.core.model.RoomOverview
import io.foundy.room_list.data.api.RecommendRoomApi
import io.foundy.room_list.data.api.RoomListApi
import io.foundy.room_list.data.model.RoomCreateRequestBody
import io.foundy.room_list.data.model.toEntity
import io.foundy.room_list.data.source.RoomPagingSource
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class PagingRoomListRepository @Inject constructor(
    private val roomListApi: RoomListApi,
    private val recommendRoomApi: RecommendRoomApi
) : RoomListRepository {

    override fun getRooms(query: String): Flow<PagingData<RoomOverview>> {
        return Pager(
            config = PagingConfig(RoomPagingSource.PAGE_SIZE),
            pagingSourceFactory = { RoomPagingSource(api = roomListApi, query = query) }
        ).flow
    }

    override suspend fun getRecentRooms(userId: String): Result<List<RoomOverview>> {
        return runCatching {
            val response = roomListApi.getRecentRooms(userId = userId)
            response.getDataOrThrowMessage().map { it.toEntity() }
        }
    }

    override suspend fun getRecommendedRooms(userId: String): Result<List<RoomOverview>> {
        return runCatching {
            val response = recommendRoomApi.getRecommendedRoom(userId = userId)
            response.getDataOrThrowMessage().rooms.map { it.toEntity() }
        }
    }

    override suspend fun createRoom(
        createRequestBody: RoomCreateRequestBody,
        thumbnail: File?
    ): Result<RoomOverview> {
        return runCatching {
            val response = roomListApi.createRoom(body = createRequestBody)
            var roomOverview = response.getDataOrThrowMessage().toEntity()

            if (thumbnail != null) {
                val multipart = MultipartBody.Part.createFormData(
                    ROOM_THUMBNAIL_KEY,
                    thumbnail.name,
                    thumbnail.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                val thumbnailResponse = roomListApi.postRoomThumbnail(
                    roomId = roomOverview.id,
                    partMap = multipart
                ).getDataOrThrowMessage()
                roomOverview = roomOverview.copy(thumbnail = thumbnailResponse)
            }
            return@runCatching roomOverview
        }
    }

    companion object {
        const val ROOM_THUMBNAIL_KEY = "roomThumbnail"
    }
}
