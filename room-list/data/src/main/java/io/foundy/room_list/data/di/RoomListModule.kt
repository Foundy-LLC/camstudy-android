package io.foundy.room_list.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.room_list.data.repository.PagingRoomListRepository
import io.foundy.room_list.data.repository.RoomListRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomListModule {

    @Binds
    @Singleton
    abstract fun bindsRoomListRepository(
        pagingRoomListRepository: PagingRoomListRepository
    ): RoomListRepository
}
