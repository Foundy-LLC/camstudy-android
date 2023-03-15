package io.foundy.room.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import io.foundy.room.data.service.RoomService
import io.foundy.room.data.service.RoomSocketService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomServiceModule {

    @Binds
    @Singleton
    abstract fun bindsRoomService(
        roomSocketService: RoomSocketService
    ): RoomService
}
