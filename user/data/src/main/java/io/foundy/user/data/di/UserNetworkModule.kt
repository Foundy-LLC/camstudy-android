package io.foundy.user.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.foundy.user.data.api.UserApi
import io.foundy.user.data.repository.NetworkUserRepository
import io.foundy.user.data.repository.UserRepository
import io.foundy.user.data.source.RetrofitUserDataSource
import io.foundy.user.data.source.UserRemoteDataSource
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UserNetworkModule {

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun providesUserRemoteDataSource(api: UserApi): UserRemoteDataSource {
        return RetrofitUserDataSource(api = api)
    }

    @Provides
    @Singleton
    fun providesUserRepository(dataSource: UserRemoteDataSource): UserRepository {
        return NetworkUserRepository(dataSource)
    }
}
