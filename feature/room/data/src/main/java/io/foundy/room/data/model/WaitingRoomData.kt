package io.foundy.room.data.model

import io.foundy.room.domain.PeerOverview
import kotlinx.serialization.Serializable

@Serializable
data class WaitingRoomData(
    /**
     * 공부방에 참여한 사람들의 목록이다.
     */
    val joinerList: List<RoomJoiner>,

    /**
     * 공부방의 최대 참여 가능한 인원이다.
     */
    val capacity: Int,

    /**
     * 공부방의 방장 ID이다.
     */
    val masterId: String,

    /**
     * 공부방의 차단 인원의 ID 목록이다.
     */
    val blacklist: List<PeerOverview>,

    /**
     * 공부방이 비밀번호를 가지고 있는 경우 `true`이다.
     */
    val hasPassword: Boolean
)
