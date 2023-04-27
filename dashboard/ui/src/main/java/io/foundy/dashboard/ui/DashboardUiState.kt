package io.foundy.dashboard.ui

import io.foundy.core.model.GrowingCrop
import io.foundy.core.model.RoomOverview
import io.foundy.core.model.UserRankingOverview

data class DashboardUiState(
    val userRankingUiState: UserRankingUiState = UserRankingUiState.Loading,
    val growingCropUiState: GrowingCropUiState = GrowingCropUiState.Loading,
    val recentRoomsUiState: RecentRoomsUiState = RecentRoomsUiState.Loading,
    val fetchGrowingCrop: () -> Unit
)

sealed class UserRankingUiState {

    object Loading : UserRankingUiState()

    data class Success(val userRanking: UserRankingOverview) : UserRankingUiState()

    data class Failure(val message: String?) : UserRankingUiState()
}

sealed class GrowingCropUiState {

    object Loading : GrowingCropUiState()

    data class Success(val growingCrop: GrowingCrop?) : GrowingCropUiState()

    data class Failure(val message: String?) : GrowingCropUiState()
}

sealed class RecentRoomsUiState {

    object Loading : RecentRoomsUiState()

    data class Success(val recentRooms: List<RoomOverview>) : RecentRoomsUiState()

    data class Failure(val message: String?) : RecentRoomsUiState()
}
