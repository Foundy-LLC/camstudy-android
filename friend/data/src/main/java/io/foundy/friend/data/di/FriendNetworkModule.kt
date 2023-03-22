package io.foundy.friend.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.friend.data.api.FriendApi
import io.foundy.friend.data.repository.FriendRepository
import io.foundy.friend.data.repository.NetworkFriendRepository
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FriendNetworkModule {

    @Provides
    @Singleton
    fun providesFriendApiService(retrofit: Retrofit): FriendApi {
        return retrofit.create(FriendApi::class.java)
    }

    @Provides
    @Singleton
    fun providesFriendRepository(api: FriendApi): FriendRepository {
        return NetworkFriendRepository(api = api)
    }
}
