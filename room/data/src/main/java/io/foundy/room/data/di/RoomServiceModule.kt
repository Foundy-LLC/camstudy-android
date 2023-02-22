package io.foundy.room.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.foundy.room.data.service.RoomService
import io.foundy.room.data.service.RoomSocketService

@Module
@InstallIn(ViewModelComponent::class)
abstract class RoomServiceModule {

    @Binds
    abstract fun bindsRoomService(
        roomSocketService: RoomSocketService
    ): RoomService
}
